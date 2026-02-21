package com.forexconverter.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.forexconverter.dto.ConversionResponse;
import com.forexconverter.dto.ErrorResponse;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Tag("unit")
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler =
      new GlobalExceptionHandler(new SimpleMeterRegistry());

  @DisplayName("Should return 400 for IllegalArgumentException")
  @Test
  void shouldReturn400ForIllegalArgument() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid currency code");
    ResponseEntity<ConversionResponse> response = handler.handleIllegalArgument(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
  }

  @DisplayName("Should return 404 for RateNotFoundException")
  @Test
  void shouldReturn404ForRateNotFound() {
    RateNotFoundException ex = new RateNotFoundException("Rate not found");
    ResponseEntity<ConversionResponse> response = handler.handleRateNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
  }

  @DisplayName("Should return 400 for ConstraintViolationException")
  @Test
  void shouldReturn400ForConstraintViolation() {
    ConstraintViolationException ex =
        new ConstraintViolationException("Amount must be at least 0.01", Set.of());
    ResponseEntity<ConversionResponse> response = handler.handleConstraintViolation(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
  }

  @DisplayName("Should return 400 for MethodArgumentTypeMismatchException")
  @Test
  void shouldReturn400ForTypeMismatch() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException("abc", BigDecimal.class, "amount", null, null);

    ResponseEntity<ConversionResponse> response = handler.handleTypeMismatch(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
  }

  @DisplayName("Should return 500 for RateProviderException")
  @Test
  void shouldReturn500ForRateProviderException() {
    RateProviderException ex = new RateProviderException("Provider error");
    ResponseEntity<ConversionResponse> response = handler.handleRateProviderException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
  }

  @DisplayName("Should return 500 for generic Exception (catch-all)")
  @Test
  void shouldReturn500ForGenericException() {
    Exception ex = new RuntimeException("Unexpected error");
    ResponseEntity<ConversionResponse> response = handler.handleGenericException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
  }
}
