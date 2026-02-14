package com.forexconverter.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.forexconverter.Currency;
import com.forexconverter.service.ConversionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConversionController.class)
@Tag("unit")
class ConversionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ConversionService conversionService;

  @DisplayName("ConversionController should return 200 with result")
  @Test
  void shouldReturnConversionResult() throws Exception {
    when(conversionService.convert(Currency.USD, Currency.EUR, new BigDecimal("100")))
        .thenReturn(new BigDecimal("92.50"));

    mockMvc
        .perform(get("/convert/USD-EUR").param("amount", "100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(92.5));
  }

  @DisplayName("ConversionController should return 200 with rate when amount is missing")
  @Test
  void shouldReturnRateWhenAmountMissing() throws Exception {
    when(conversionService.convert(Currency.USD, Currency.EUR, null))
        .thenReturn(new BigDecimal("0.9250"));

    mockMvc
        .perform(get("/convert/USD-EUR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value(0.925));
  }

  @DisplayName("ConversionController should return 400 for invalid amount")
  @Test
  void shouldReturn400ForInvalidAmount() throws Exception {
    mockMvc
        .perform(get("/convert/USD-EUR").param("amount", "abc"))
        .andExpect(status().isBadRequest());
  }
}
