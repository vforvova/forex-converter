package com.forexconverter.swop;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
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

  @Test
  void shouldFetchAllRatesFromSwop() throws InterruptedException {
    mockServer.enqueue(
        new MockResponse()
            .setBody(
                """
                [
                    {
                        "base_currency": "EUR",
                        "quote_currency": "USD",
                        "quote": 1.079301,
                        "date": "2026-02-22"
                    },
                    {
                        "base_currency": "EUR",
                        "quote_currency": "GBP",
                        "quote": 0.852341,
                        "date": "2026-02-22"
                    }
                ]
                """)
            .addHeader("Content-Type", "application/json"));

    Client client = new Client(properties);
    AllRatesResponse response = client.fetchAllRates();

    assertThat(response.rates()).hasSize(2);
    assertThat(response.rates().get(0).baseCurrency()).isEqualTo("EUR");
    assertThat(response.rates().get(0).quoteCurrency()).isEqualTo("USD");
    assertThat(response.rates().get(0).quote()).isEqualTo(new BigDecimal("1.079301"));
    assertThat(response.rates().get(1).quoteCurrency()).isEqualTo("GBP");
    assertThat(response.rates().get(1).quote()).isEqualTo(new BigDecimal("0.852341"));

    RecordedRequest request = mockServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/rest/rates");
    assertThat(request.getHeader("Authorization")).isEqualTo("ApiKey test-key");
  }
}
