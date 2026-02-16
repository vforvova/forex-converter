package com.forexconverter.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class SwopRateResponseTest {

  @Test
  void shouldDeserializeFromJson() throws Exception {
    String json =
        """
            {
                "base_currency": "EUR",
                "quote_currency": "USD",
                "quote": 1.079301,
                "date": "2026-02-15"
            }
            """;

    ObjectMapper mapper = new ObjectMapper();
    SwopRateResponse response = mapper.readValue(json, SwopRateResponse.class);

    assertThat(response.baseCurrency()).isEqualTo("EUR");
    assertThat(response.quoteCurrency()).isEqualTo("USD");
    assertThat(response.quote()).isEqualTo(new BigDecimal("1.079301"));
    assertThat(response.date()).isEqualTo("2026-02-15");
  }
}
