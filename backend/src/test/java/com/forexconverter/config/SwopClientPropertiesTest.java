package com.forexconverter.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    properties = {"swop.base-url=https://swop.cx", "swop.api-key=test-key", "swop.timeout-ms=1000"})
class SwopClientPropertiesTest {

  @Autowired private SwopClientProperties properties;

  @Test
  void shouldBindBaseUrl() {
    assertEquals("https://swop.cx", properties.baseUrl());
  }

  @Test
  void shouldBindApiKey() {
    assertEquals("test-key", properties.apiKey());
  }

  @Test
  void shouldBindTimeoutMs() {
    assertEquals(1000, properties.timeoutMs());
  }
}
