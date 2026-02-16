package com.forexconverter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record SwopRateResponse(
    @JsonProperty("base_currency") String baseCurrency,
    @JsonProperty("quote_currency") String quoteCurrency,
    @JsonProperty("quote") BigDecimal quote,
    @JsonProperty("date") String date) {}
