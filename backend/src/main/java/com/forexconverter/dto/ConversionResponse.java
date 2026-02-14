package com.forexconverter.dto;

import java.math.BigDecimal;

public record ConversionResponse(BigDecimal result, String error) {

  public static ConversionResponse success(BigDecimal result) {
    return new ConversionResponse(result, null);
  }

  public static ConversionResponse error(String error) {
    return new ConversionResponse(null, error);
  }
}
