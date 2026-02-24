package com.forexconverter.conversion;

import com.forexconverter.rate.ProviderException;
import com.forexconverter.rate.ProviderRateNotFoundException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ConstraintViolationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ConversionExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ConversionExceptionHandler.class);

  private final MeterRegistry meterRegistry;
  private final ConcurrentMap<String, Counter> errorCounters = new ConcurrentHashMap<>();

  public ConversionExceptionHandler(MeterRegistry meterRegistry) {
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

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
    getErrorCounter("IllegalArgumentException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO("Invalid currency code: " + ex.getMessage()));
  }

  @ExceptionHandler(ProviderRateNotFoundException.class)
  public ResponseEntity<ResponseDTO> handleRateNotFound(ProviderRateNotFoundException ex) {
    getErrorCounter("ProviderRateNotFoundException").increment();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
    getErrorCounter("ConstraintViolationException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO(ex.getMessage()));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ResponseDTO> handleHandlerValidation(HandlerMethodValidationException ex) {
    getErrorCounter("HandlerMethodValidationException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO("Validation failed"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    getErrorCounter("MethodArgumentTypeMismatchException").increment();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO("Invalid parameter"));
  }

  @ExceptionHandler(ProviderException.class)
  public ResponseEntity<ResponseDTO> handleRateProviderException(ProviderException ex) {
    getErrorCounter("ProviderException").increment();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDTO(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDTO> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    getErrorCounter("UnexpectedException").increment();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDTO("An unexpected error occurred"));
  }
}
