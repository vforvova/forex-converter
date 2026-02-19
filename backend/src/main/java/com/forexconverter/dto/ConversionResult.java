package com.forexconverter.dto;

import java.math.BigDecimal;

public record ConversionResult(BigDecimal result) implements ConversionResponse {}
