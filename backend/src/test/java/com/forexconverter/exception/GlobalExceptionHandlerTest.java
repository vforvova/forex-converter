package com.forexconverter.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.forexconverter.Currency;
import com.forexconverter.dto.ConversionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Tag("unit")
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @DisplayName("Should return 404 for InvalidCurrencyException")
  @Test
  void shouldReturn404ForInvalidCurrency() {
    InvalidCurrencyException ex = new InvalidCurrencyException("Currency XXX not registered");
    ResponseEntity<ConversionResponse> response = handler.handleInvalidCurrency(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @DisplayName("Should return 404 for RateNotFoundException")
  @Test
  void shouldReturn404ForRateNotFound() {
    RateNotFoundException ex = new RateNotFoundException("Rate not found");
    ResponseEntity<ConversionResponse> response = handler.handleRateNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @DisplayName("Should return 400 for SameCurrencyConversionException")
  @Test
  void shouldReturn400ForSameCurrency() {
    SameCurrencyConversionException ex = new SameCurrencyConversionException();
    ResponseEntity<ConversionResponse> response = handler.handleSameCurrency(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @DisplayName("Should return 500 for RateProviderException")
  @Test
  void shouldReturn500ForRateProviderException() {
    RateProviderException ex = new RateProviderException("Provider error");
    ResponseEntity<ConversionResponse> response = handler.handleRateProviderException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @DisplayName("Should return 400 for non-enum MethodArgumentTypeMismatchException")
  @Test
  void shouldReturn400ForNonEnumTypeMismatch() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("XXX", String.class, "param", null, null);

    ResponseEntity<ConversionResponse> response = handler.handleTypeMismatch(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @DisplayName("Should return 404 for enum type mismatch")
  @Test
  void shouldReturn404ForEnumTypeMismatch() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("XXX", Currency.class, "currency", null, null);

    ResponseEntity<ConversionResponse> response = handler.handleTypeMismatch(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().error()).contains("Currency not found: XXX");
  }
}
