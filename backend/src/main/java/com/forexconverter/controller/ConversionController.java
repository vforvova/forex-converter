package com.forexconverter.controller;

import com.forexconverter.Currency;
import com.forexconverter.dto.ConversionResponse;
import com.forexconverter.service.ConversionService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
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
  public ResponseEntity<ConversionResponse> convert(
      @PathVariable Currency from,
      @PathVariable Currency to,
      @RequestParam(required = false)
          @Digits(integer = 15, fraction = 2, message = "Amount must have at most 2 decimal places")
          @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
          @DecimalMax(value = "100000000000", message = "Amount must be at most 100000000000")
          BigDecimal amount) {
    BigDecimal result = service.convert(from, to, amount);
    return ResponseEntity.ok(ConversionResponse.success(result));
  }
}
