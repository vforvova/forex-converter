package com.forexconverter.rate;

import com.forexconverter.swop.Client;
import com.forexconverter.swop.RateResponseDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class SwopProvider implements Provider {

  private static final Logger log = LoggerFactory.getLogger(SwopProvider.class);

  private final Client client;
  private final Cache cache;

  private final Counter cacheHitCounter;
  private final Counter cacheMissCounter;

  public SwopProvider(Client client, CacheManager cacheManager, MeterRegistry registry) {
    this.client = client;
    this.cache = cacheManager.getCache(CacheConfig.EXCHANGE_RATES_CACHE);

    this.cacheHitCounter =
        Counter.builder("cache.gets")
            .description("Number of cache get operations")
            .tag("result", "hit")
            .register(registry);

    this.cacheMissCounter =
        Counter.builder("cache.gets")
            .description("Number of cache get operations")
            .tag("result", "miss")
            .register(registry);
  }

  @Override
  public BigDecimal getRate(Currency from, Currency to) {
    String todayKey = buildKey(LocalDate.now(), from, to);
    BigDecimal cached = cache.get(todayKey, BigDecimal.class);

    if (cached != null) {
      cacheHitCounter.increment();
      return cached;
    }
    cacheMissCounter.increment();

    try {
      ResponseEntity<RateResponseDTO> response =
          client.fetchRate(from.getCurrencyCode(), to.getCurrencyCode());

      Rate rate = mapToRate(response.getBody());
      cache.put(todayKey, rate.rate());
      return rate.rate();
    } catch (Exception e) {
      log.error("Failed to fetch rate for {} -> {}: {}", from, to, e.getMessage(), e);
      throw wrapException(e);
    }
  }

  public void warmupCache() {
    try {
      ResponseEntity<java.util.List<RateResponseDTO>> response = client.fetchAllRates();

      LocalDate today = LocalDate.now();
      for (RateResponseDTO rate : response.getBody()) {
        String key =
            buildKey(
                today,
                Currency.getInstance(rate.baseCurrency()),
                Currency.getInstance(rate.quoteCurrency()));
        cache.put(key, rate.quote());
      }
      log.info("Cache warmup completed, loaded {} rates", response.getBody().size());
    } catch (Exception e) {
      log.error("Cache warmup failed: {}", e.getMessage(), e);
    }
  }

  private String buildKey(LocalDate date, Currency from, Currency to) {
    return date + ":" + from + ":" + to;
  }

  private RuntimeException wrapException(Exception e) {
    if (e instanceof HttpClientErrorException.NotFound) {
      return new ProviderRateNotFoundException("Rate not found", e);
    }

    if (e instanceof HttpClientErrorException) {
      return new ProviderException("Failed to fetch rate from provider", e);
    }

    if (e instanceof HttpServerErrorException) {
      return new ProviderException("Rate provider server error", e);
    }

    if (e instanceof ResourceAccessException) {
      return new ProviderException("Rate provider unreachable", e);
    }

    if (e instanceof HttpMessageConversionException) {
      return new ProviderException("Failed to parse provider response", e);
    }

    return new ProviderException("Unexpected error fetching rate", e);
  }

  private Rate mapToRate(RateResponseDTO response) {
    return new Rate(
        Currency.getInstance(response.baseCurrency()),
        Currency.getInstance(response.quoteCurrency()),
        response.quote(),
        LocalDate.parse(response.date()));
  }
}
