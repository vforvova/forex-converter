package com.forexconverter.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "swop.api-key=test-api-key")
@Tag("integration")
class ConversionControllerIntegrationTest {

  @LocalServerPort private int port;

  private final HttpClient client = HttpClient.newHttpClient();
  private static MockWebServer mockServer;
  private static String mockServerUrl;

  @BeforeAll
  static void setUpAll() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start(8089);
    mockServerUrl = mockServer.url("/").toString();
  }

  @AfterAll
  static void tearDownAll() throws IOException {
    mockServer.shutdown();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("swop.base-url", () -> mockServerUrl);
  }

  private String url(String path) {
    return "http://localhost:" + port + path;
  }

  // -- Error scenarios --

  private static Stream<Arguments> errorScenarios() {
    return Stream.of(
        Arguments.of("/convert/USD-EUR?amount=-100", 400),
        Arguments.of("/convert/USD-EUR?amount=abc", 400),
        Arguments.of("/convert/USD-XYZ?amount=100", 400),
        Arguments.of("/convert/XYZ-EUR?amount=100", 400));
  }

  @DisplayName("Should respond with errors")
  @ParameterizedTest
  @MethodSource("errorScenarios")
  void shouldRespondWithErrors(String urlPath, int expectedStatus) throws Exception {
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url(urlPath))).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(expectedStatus);
  }

  // -- Cross-currency success scenarios --

  private static Stream<Arguments> crossCurrencySuccessScenarios() {
    return Stream.of(
        Arguments.of("/convert/EUR-USD?amount=100", "EUR", "USD", "1.079301", "107.9301"),
        Arguments.of("/convert/USD-GBP?amount=50", "USD", "GBP", "0.789123", "39.45615"));
  }

  @DisplayName("Should return 200 for cross-currency conversions")
  @ParameterizedTest
  @MethodSource("crossCurrencySuccessScenarios")
  void shouldConvertCrossCurrencies(
      String urlPath, String from, String to, String rate, String expectedResult) throws Exception {
    mockServer.enqueue(
        new MockResponse()
            .setBody(
                String.format(
                    """
                    {
                        "base_currency": "%s",
                        "quote_currency": "%s",
                        "quote": %s,
                        "date": "2026-02-15"
                    }
                    """,
                    from, to, rate))
            .addHeader("Content-Type", "application/json"));

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url(urlPath))).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("\"result\":" + expectedResult);
  }

  // -- Same-currency success scenarios --

  private static Stream<Arguments> sameCurrencySuccessScenarios() {
    return Stream.of(
        Arguments.of("/convert/USD-USD?amount=100", "100"), Arguments.of("/convert/EUR-EUR", "1"));
  }

  @DisplayName("Should return 200 for same-currency conversions (identity)")
  @ParameterizedTest
  @MethodSource("sameCurrencySuccessScenarios")
  void shouldReturnIdentityForSameCurrency(String urlPath, String expectedResult) throws Exception {

    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url(urlPath))).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("\"result\":" + expectedResult);
  }
}
