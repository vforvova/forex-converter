package com.forexconverter.exception;

import com.forexconverter.dto.ConversionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SameCurrencyConversionException.class)
  public ResponseEntity<ConversionResponse> handleSameCurrency(SameCurrencyConversionException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(ConversionException.class)
  public ResponseEntity<ConversionResponse> handleConversionException(ConversionException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ConversionResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ConversionResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    // When the parameter type is not an enum (e.g., amount is not a valid number), return generic
    // bad request
    if (ex.getRequiredType() == null || !ex.getRequiredType().isEnum()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ConversionResponse.error("Invalid parameter"));
    }

    // When an enum value doesn't match any defined constant (e.g., XXX is not a valid Currency),
    // return not found
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ConversionResponse.error("Currency not found: " + ex.getValue()));
  }
}
