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

  @DisplayName("GlobalExceptionHandler should return 404 for ConversionException")
  @Test
  void shouldReturn404ForConversionException() {
    ConversionException ex = new ConversionException("Currency not found: XXX");
    ResponseEntity<ConversionResponse> response = handler.handleConversionException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().error()).isEqualTo("Currency not found: XXX");
  }

  @DisplayName("GlobalExceptionHandler should return 400 for same currency conversion")
  @Test
  void shouldReturn400ForSameCurrency() {
    SameCurrencyConversionException ex = new SameCurrencyConversionException();
    ResponseEntity<ConversionResponse> response = handler.handleSameCurrency(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().error())
        .isEqualTo("Source and target currency cannot be the same");
  }

  @DisplayName("GlobalExceptionHandler should return 404 for enum type mismatch")
  @Test
  void shouldReturn404ForEnumMismatch() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("XXX", Currency.class, "currency", null, null);
    ResponseEntity<ConversionResponse> response = handler.handleTypeMismatch(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().error()).isEqualTo("Currency not found: XXX");
  }
}
