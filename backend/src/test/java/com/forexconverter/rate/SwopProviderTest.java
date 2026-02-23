package com.forexconverter.rate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.forexconverter.swop.AllRatesResponse;
import com.forexconverter.swop.Client;
import com.forexconverter.swop.RateResponseDTO;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class SwopProviderTest {

  @Mock private Client client;
  @Mock private CacheManager cacheManager;
  @Mock private Cache cache;

  private SwopProvider provider;
  private LocalDate today;

  @BeforeEach
  void setUp() {
    when(cacheManager.getCache("exchangeRates")).thenReturn(cache);
    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    provider = new SwopProvider(client, cacheManager, meterRegistry);
    today = LocalDate.now();
  }

  @Nested
  @DisplayName("getRate()")
  class GetRateTests {

    @Test
    @DisplayName("Should return rate on successful response")
    void shouldReturnRate() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");

      BigDecimal rateValue = new BigDecimal("1.079301");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenReturn(new RateResponseDTO("USD", "EUR", rateValue, "2026-02-15"));

      BigDecimal result = provider.getRate(from, to);

      assertThat(result).isEqualTo(rateValue);
    }

    @Test
    @DisplayName("Should return cached rate when available")
    void shouldReturnCachedRate() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");

      BigDecimal cachedRate = new BigDecimal("1.08");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(cachedRate);

      BigDecimal result = provider.getRate(from, to);

      assertThat(result).isEqualTo(cachedRate);
      verify(client, never()).fetchRate("USD", "EUR");
    }

    @Test
    @DisplayName("Should call swop API and cache result when cache miss")
    void shouldCallApiOnCacheMiss() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");

      BigDecimal rateValue = new BigDecimal("1.079301");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenReturn(new RateResponseDTO("USD", "EUR", rateValue, "2026-02-15"));

      BigDecimal result = provider.getRate(from, to);

      assertThat(result).isEqualTo(rateValue);
      verify(client, times(1)).fetchRate("USD", "EUR");
      verify(cache).put(todayKey, rateValue);
    }

    @Test
    @DisplayName("Should throw ProviderRateNotFoundException when rate not found")
    void shouldThrowWhenNotFound() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenThrow(
              HttpClientErrorException.NotFound.create(
                  HttpStatus.NOT_FOUND, "Not Found", null, null, null));

      assertThatThrownBy(() -> provider.getRate(from, to))
          .isInstanceOf(ProviderRateNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ProviderException for client errors")
    void shouldThrowForClientError() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

      assertThatThrownBy(() -> provider.getRate(from, to)).isInstanceOf(ProviderException.class);
    }

    @Test
    @DisplayName("Should throw ProviderException for server errors")
    void shouldThrowForServerError() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

      assertThatThrownBy(() -> provider.getRate(from, to)).isInstanceOf(ProviderException.class);
    }

    @Test
    @DisplayName("Should throw ProviderException when connection fails")
    void shouldThrowWhenConnectionFails() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenThrow(new ResourceAccessException("Connection refused"));

      assertThatThrownBy(() -> provider.getRate(from, to)).isInstanceOf(ProviderException.class);
    }

    @Test
    @DisplayName("Should throw ProviderException when response parsing fails")
    void shouldThrowWhenParseFails() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR"))
          .thenThrow(new HttpMessageConversionException("Invalid JSON"));

      assertThatThrownBy(() -> provider.getRate(from, to)).isInstanceOf(ProviderException.class);
    }

    @Test
    @DisplayName("Should throw ProviderException for unexpected errors")
    void shouldThrowForUnexpectedError() {
      Currency from = Currency.getInstance("USD");
      Currency to = Currency.getInstance("EUR");
      String todayKey = today + ":USD:EUR";

      when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
      when(client.fetchRate("USD", "EUR")).thenThrow(new RuntimeException("Unexpected"));

      assertThatThrownBy(() -> provider.getRate(from, to)).isInstanceOf(ProviderException.class);
    }

    @Test
    @DisplayName("Should use same cache key format as getRate() for warmup")
    void shouldUseSameCacheKeyFormat() {
      List<RateResponseDTO> rates =
          List.of(new RateResponseDTO("EUR", "USD", new BigDecimal("1.079301"), today.toString()));

      when(client.fetchAllRates()).thenReturn(new AllRatesResponse(rates));

      provider.warmupCache();

      String expectedKey = today + ":EUR:USD";
      verify(cache).put(expectedKey, new BigDecimal("1.079301"));
    }
  }

  @Nested
  @DisplayName("warmupCache()")
  class WarmupTests {

    @Test
    @DisplayName("Should warmup cache with all rates from bulk endpoint")
    void shouldWarmupCacheWithAllRates() {
      List<RateResponseDTO> rates =
          List.of(
              new RateResponseDTO("EUR", "USD", new BigDecimal("1.079301"), today.toString()),
              new RateResponseDTO("EUR", "GBP", new BigDecimal("0.852341"), today.toString()));

      when(client.fetchAllRates()).thenReturn(new AllRatesResponse(rates));

      provider.warmupCache();

      verify(client).fetchAllRates();
      verify(cache).put(today + ":EUR:USD", new BigDecimal("1.079301"));
      verify(cache).put(today + ":EUR:GBP", new BigDecimal("0.852341"));
    }

    @Test
    @DisplayName("Should log error and not throw when bulk fetch fails")
    void shouldNotThrowOnBulkFetchFailure() {
      when(client.fetchAllRates()).thenThrow(new RuntimeException("Connection failed"));

      assertThatCode(() -> provider.warmupCache()).doesNotThrowAnyException();

      verify(client).fetchAllRates();
    }
  }
}
