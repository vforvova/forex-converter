package com.forexconverter.dto;

public sealed interface ConversionResponse permits ConversionResult, ErrorResponse {}
