package com.forexconverter.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
    properties = {"swop.base-url=https://swop.cx", "swop.api-key=test-key", "swop.timeout=1000ms"})
class SwopClientPropertiesTest {

  @Autowired private SwopClientProperties properties;

  @Test
  void shouldBindBaseUrl() {
    assertThat(properties.baseUrl()).isEqualTo("https://swop.cx");
  }

  @Test
  void shouldBindApiKey() {
    assertThat(properties.apiKey()).isEqualTo("test-key");
  }

  @Test
  void shouldBindTimeout() {
    assertThat(properties.timeout()).isEqualTo(Duration.ofMillis(1000));
  }
}
