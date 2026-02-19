package com.forexconverter.exception;

import com.forexconverter.dto.ConversionResponse;
import com.forexconverter.dto.ErrorResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ConstraintViolationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private final MeterRegistry meterRegistry;
  private final ConcurrentMap<String, Counter> errorCounters = new ConcurrentHashMap<>();

  public GlobalExceptionHandler(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  private Counter getErrorCounter(String errorType) {
    return errorCounters.computeIfAbsent(
        errorType,
        et ->
            Counter.builder("controller.errors")
                .description("Number of controller errors by type")
                .tag("type", et)
                .register(meterRegistry));
  }

  @ExceptionHandler(InvalidCurrencyException.class)
  public ResponseEntity<ConversionResponse> handleInvalidCurrency(InvalidCurrencyException ex) {
    getErrorCounter("InvalidCurrencyException").increment();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(RateNotFoundException.class)
  public ResponseEntity<ConversionResponse> handleRateNotFound(RateNotFoundException ex) {
    getErrorCounter("RateNotFoundException").increment();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(SameCurrencyConversionException.class)
  public ResponseEntity<ConversionResponse> handleSameCurrency(SameCurrencyConversionException ex) {
    getErrorCounter("SameCurrencyConversionException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ConversionResponse> handleConstraintViolation(
      ConstraintViolationException ex) {
    getErrorCounter("ConstraintViolationException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ConversionResponse> handleHandlerValidation(
      HandlerMethodValidationException ex) {
    getErrorCounter("HandlerMethodValidationException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Validation failed"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ConversionResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    if (ex.getRequiredType() == null || !ex.getRequiredType().isEnum()) {
      getErrorCounter("MethodArgumentTypeMismatchException").increment();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorResponse("Invalid parameter"));
    }
    getErrorCounter("CurrencyNotFound").increment();
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("Currency not found: " + ex.getValue()));
  }

  @ExceptionHandler(RateProviderException.class)
  public ResponseEntity<ConversionResponse> handleRateProviderException(RateProviderException ex) {
    getErrorCounter("RateProviderException").increment();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ConversionResponse> handleGenericException(Exception ex) {
    getErrorCounter("UnexpectedException").increment();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("An unexpected error occurred"));
  }
}
