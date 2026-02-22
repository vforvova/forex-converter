package com.forexconverter.swop;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {

  private MockWebServer mockServer;
  private ClientProperties properties;

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    properties = new ClientProperties();
    properties.setBaseUrl(mockServer.url("/").toString());
    properties.setApiKey("test-key");
    properties.setTimeout(Duration.ofMillis(1000));
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Test
  void shouldFetchRateFromSwop() throws InterruptedException {
    mockServer.enqueue(
        new MockResponse()
            .setBody(
                """
                {
                    "base_currency": "EUR",
                    "quote_currency": "USD",
                    "quote": 1.079301,
                    "date": "2026-02-15"
                }
                """)
            .addHeader("Content-Type", "application/json"));

    Client client = new Client(properties);
    RateResponseDTO response = client.fetchRate("EUR", "USD");

    assertThat(response.baseCurrency()).isEqualTo("EUR");
    assertThat(response.quoteCurrency()).isEqualTo("USD");
    assertThat(response.quote()).isEqualTo(new BigDecimal("1.079301"));
    assertThat(response.date()).isEqualTo("2026-02-15");

    RecordedRequest request = mockServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/rest/rates/EUR/USD");
    assertThat(request.getHeader("Authorization")).isEqualTo("ApiKey test-key");
  }
}
