package com.forexconverter.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.forexconverter.Currency;
import com.forexconverter.client.SwopClient;
import com.forexconverter.dto.SwopRateResponse;
import com.forexconverter.exception.RateNotFoundException;
import com.forexconverter.exception.RateProviderException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class SwopRateProviderTest {

  @Mock private SwopClient swopClient;
  @Mock private CacheManager cacheManager;
  @Mock private Cache cache;

  private SwopRateProvider provider;
  private LocalDate today;

  @BeforeEach
  void setUp() {
    when(cacheManager.getCache("exchangeRates")).thenReturn(cache);
    provider = new SwopRateProvider(swopClient, cacheManager);
    today = LocalDate.now();
  }

  @DisplayName("Should return rate on successful response")
  @Test
  void shouldReturnRate() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenReturn(new SwopRateResponse("USD", "EUR", new BigDecimal("1.079301"), "2026-02-15"));

    BigDecimal rate = provider.getRate(Currency.USD, Currency.EUR);

    assertThat(rate).isEqualTo(new BigDecimal("1.079301"));
  }

  @DisplayName("Should return cached rate when available")
  @Test
  void shouldReturnCachedRate() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(new BigDecimal("1.08"));

    BigDecimal rate = provider.getRate(Currency.USD, Currency.EUR);

    assertThat(rate).isEqualTo(new BigDecimal("1.08"));
    verify(swopClient, never()).fetchRate("USD", "EUR");
  }

  @DisplayName("Should call swop API and cache result when cache miss")
  @Test
  void shouldCallApiOnCacheMiss() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenReturn(new SwopRateResponse("USD", "EUR", new BigDecimal("1.079301"), "2026-02-15"));

    BigDecimal rate = provider.getRate(Currency.USD, Currency.EUR);

    assertThat(rate).isEqualTo(new BigDecimal("1.079301"));
    verify(swopClient, times(1)).fetchRate("USD", "EUR");
    verify(cache).put(todayKey, new BigDecimal("1.079301"));
  }

  @DisplayName("Should throw RateNotFoundException when rate not found")
  @Test
  void shouldThrowWhenNotFound() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenThrow(
            HttpClientErrorException.NotFound.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null));

    assertThatThrownBy(() -> provider.getRate(Currency.USD, Currency.EUR))
        .isInstanceOf(RateNotFoundException.class)
        .hasMessage("Rate not found");
  }

  @DisplayName("Should throw RateProviderException for other 4xx errors")
  @Test
  void shouldThrowForClientError() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

    assertThatThrownBy(() -> provider.getRate(Currency.USD, Currency.EUR))
        .isInstanceOf(RateProviderException.class)
        .hasMessage("Failed to fetch rate from provider");
  }

  @DisplayName("Should throw RateProviderException for 5xx server errors")
  @Test
  void shouldThrowForServerError() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    assertThatThrownBy(() -> provider.getRate(Currency.USD, Currency.EUR))
        .isInstanceOf(RateProviderException.class)
        .hasMessage("Rate provider server error");
  }

  @DisplayName("Should throw RateProviderException when connection fails")
  @Test
  void shouldThrowWhenConnectionFails() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenThrow(new ResourceAccessException("Connection refused"));

    assertThatThrownBy(() -> provider.getRate(Currency.USD, Currency.EUR))
        .isInstanceOf(RateProviderException.class)
        .hasMessage("Rate provider unreachable");
  }

  @DisplayName("Should throw RateProviderException when response parsing fails")
  @Test
  void shouldThrowWhenParseFails() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR"))
        .thenThrow(new HttpMessageConversionException("Invalid JSON"));

    assertThatThrownBy(() -> provider.getRate(Currency.USD, Currency.EUR))
        .isInstanceOf(RateProviderException.class)
        .hasMessage("Failed to parse provider response");
  }

  @DisplayName("Should throw RateProviderException for unexpected errors")
  @Test
  void shouldThrowForUnexpectedError() {
    String todayKey = today + ":USD:EUR";
    when(cache.get(todayKey, BigDecimal.class)).thenReturn(null);
    when(swopClient.fetchRate("USD", "EUR")).thenThrow(new RuntimeException("Unexpected"));

    assertThatThrownBy(() -> provider.getRate(Currency.USD, Currency.EUR))
        .isInstanceOf(RateProviderException.class)
        .hasMessage("Unexpected error fetching rate");
  }
}
