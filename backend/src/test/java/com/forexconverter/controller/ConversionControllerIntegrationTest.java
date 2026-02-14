package com.forexconverter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class ConversionControllerIntegrationTest {

  @LocalServerPort private int port;

  private final HttpClient client = HttpClient.newHttpClient();

  private String url(String path) {
    return "http://localhost:" + port + path;
  }

  @DisplayName("Should successfully convert USD to EUR with specified amount")
  @Test
  void shouldConvertCurrencies() throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create(url("/convert/USD-EUR?amount=100"))).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("\"result\":92.5");
  }

  @DisplayName("Should handle various conversion scenarios including validation and error cases")
  @ParameterizedTest
  @CsvSource({
    "/convert/USD-EUR?amount=100, 200, 92.5",
    "/convert/USD-EUR, 200, 0.925",
    "/convert/XXX-EUR?amount=100, 404, null",
    "/convert/USD-XXX?amount=100, 404, null",
    "/convert/USD-EUR?amount=-100, 400, null"
  })
  void shouldHandleAllScenarios(String url, int expectedStatus, String expectedResult)
      throws Exception {
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url(url))).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(expectedStatus);
    if (expectedResult != null && !expectedResult.equals("null")) {
      assertThat(response.body()).contains("\"result\":" + expectedResult);
    }
  }

  @DisplayName("Should return 400 when converting same currency")
  @Test
  void shouldReturn400ForSameCurrency() throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create(url("/convert/USD-USD?amount=100"))).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(400);
    assertThat(response.body()).contains("\"error\"");
    assertThat(response.body()).contains("Source and target currency cannot be the same");
  }
}
