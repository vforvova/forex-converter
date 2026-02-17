package com.forexconverter.exception;

import com.forexconverter.dto.ConversionResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidCurrencyException.class)
  public ResponseEntity<ConversionResponse> handleInvalidCurrency(InvalidCurrencyException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(RateNotFoundException.class)
  public ResponseEntity<ConversionResponse> handleRateNotFound(RateNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(SameCurrencyConversionException.class)
  public ResponseEntity<ConversionResponse> handleSameCurrency(SameCurrencyConversionException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ConversionResponse> handleConstraintViolation(
      ConstraintViolationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ConversionResponse> handleHandlerValidation(
      HandlerMethodValidationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ConversionResponse.error("Validation failed"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ConversionResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    if (ex.getRequiredType() == null || !ex.getRequiredType().isEnum()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ConversionResponse.error("Invalid parameter"));
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ConversionResponse.error("Currency not found: " + ex.getValue()));
  }

  @ExceptionHandler(RateProviderException.class)
  public ResponseEntity<ConversionResponse> handleRateProviderException(RateProviderException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ConversionResponse> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ConversionResponse.error("An unexpected error occurred"));
  }
}
