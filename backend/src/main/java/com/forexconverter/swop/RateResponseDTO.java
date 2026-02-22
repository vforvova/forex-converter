package com.forexconverter.swop;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record RateResponseDTO(
    @JsonProperty("base_currency") String baseCurrency,
    @JsonProperty("quote_currency") String quoteCurrency,
    @JsonProperty("quote") BigDecimal quote,
    @JsonProperty("date") String date) {}
