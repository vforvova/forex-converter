package com.forexconverter.provider;

import com.forexconverter.Currency;
import com.forexconverter.client.SwopClient;
import com.forexconverter.config.CacheConfig;
import com.forexconverter.dto.SwopRateResponse;
import com.forexconverter.exception.RateNotFoundException;
import com.forexconverter.exception.RateProviderException;
import com.forexconverter.model.ExchangeRate;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class SwopRateProvider implements RateProvider {

  private final SwopClient swopClient;
  private final Cache cache;
  private final MeterRegistry meterRegistry;

  private final Counter cacheHitCounter;
  private final Counter cacheMissCounter;
  private final ConcurrentMap<String, Counter> apiSuccessCounters = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Counter> apiErrorCounters = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Timer> latencyTimers = new ConcurrentHashMap<>();

  public SwopRateProvider(
      SwopClient swopClient, CacheManager cacheManager, MeterRegistry registry) {
    this.swopClient = swopClient;
    this.cache = cacheManager.getCache(CacheConfig.EXCHANGE_RATES_CACHE);
    this.meterRegistry = registry;

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

  private Counter getApiSuccessCounter(String pair) {
    return apiSuccessCounters.computeIfAbsent(
        pair,
        p ->
            Counter.builder("swop.api.calls")
                .description("Number of SWOP API calls")
                .tag("status", "success")
                .tag("pair", p)
                .register(meterRegistry));
  }

  private Counter getApiErrorCounter(String errorType) {
    return apiErrorCounters.computeIfAbsent(
        errorType,
        et ->
            Counter.builder("swop.api.calls")
                .description("Number of SWOP API errors")
                .tag("status", "error")
                .tag("error", et)
                .register(meterRegistry));
  }

  private Timer getLatencyTimer(String pair) {
    return latencyTimers.computeIfAbsent(
        pair,
        p ->
            Timer.builder("swop.api.latency")
                .description("SWOP API call latency")
                .tag("pair", p)
                .register(meterRegistry));
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

    Timer.Sample sample = Timer.start();
    try {
      SwopRateResponse response = swopClient.fetchRate(from.name(), to.name());
      String pair = from.name() + ":" + to.name();
      getApiSuccessCounter(pair).increment();

      ExchangeRate exchangeRate = mapToExchangeRate(response);
      cache.put(todayKey, exchangeRate.rate());
      return exchangeRate.rate();
    } catch (Exception e) {
      getApiErrorCounter(e.getClass().getSimpleName()).increment();
      throw wrapException(e);
    } finally {
      String pair = from.name() + ":" + to.name();
      sample.stop(getLatencyTimer(pair));
    }
  }

  private String buildKey(LocalDate date, Currency from, Currency to) {
    return date + ":" + from + ":" + to;
  }

  private RuntimeException wrapException(Exception e) {
    if (e instanceof HttpClientErrorException.NotFound) {
      return new RateNotFoundException("Rate not found", e);
    }

    if (e instanceof HttpClientErrorException) {
      return new RateProviderException("Failed to fetch rate from provider", e);
    }

    if (e instanceof HttpServerErrorException) {
      return new RateProviderException("Rate provider server error", e);
    }

    if (e instanceof ResourceAccessException) {
      return new RateProviderException("Rate provider unreachable", e);
    }

    if (e instanceof HttpMessageConversionException) {
      return new RateProviderException("Failed to parse provider response", e);
    }

    return new RateProviderException("Unexpected error fetching rate", e);
  }

  private ExchangeRate mapToExchangeRate(SwopRateResponse response) {
    return new ExchangeRate(
        Currency.valueOf(response.baseCurrency()),
        Currency.valueOf(response.quoteCurrency()),
        response.quote(),
        LocalDate.parse(response.date()));
  }
}
