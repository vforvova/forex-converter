package com.forexconverter.conversion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConversionController {
  private final ConversionService service;

  public ConversionController(ConversionService service) {
    this.service = service;
  }

  @GetMapping("/convert/{from}-{to}")
  public ResponseEntity<ResponseDTO> convert(
      @PathVariable String from,
      @PathVariable String to,
      @RequestParam(required = false)
          @Valid
          @Digits(integer = 15, fraction = 2, message = "Amount must have at most 2 decimal places")
          @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
          @DecimalMax(value = "100000000000", message = "Amount must be at most 100000000000")
          BigDecimal amount) {
    Currency fromCurrency = Currency.getInstance(from);
    Currency toCurrency = Currency.getInstance(to);
    BigDecimal result = service.convert(fromCurrency, toCurrency, amount);
    return ResponseEntity.ok(new SuccessResponseDTO(result));
  }
}
