package com.forexconverter.swop;

import io.micrometer.core.instrument.Timer;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class Client {

  private final RestClient restClient;
  private final Metrics metrics;

  public Client(ClientProperties properties, Metrics metrics) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(properties.timeout());
    requestFactory.setReadTimeout(properties.timeout());

    this.restClient =
        RestClient.builder()
            .baseUrl(properties.baseUrl())
            .defaultHeader("Authorization", "ApiKey " + properties.apiKey())
            .requestFactory(requestFactory)
            .build();

    this.metrics = metrics;
  }

  public ResponseEntity<RateResponseDTO> fetchRate(String baseCurrency, String quoteCurrency) {
    Timer.Sample sample = metrics.startSample();
    ResponseEntity<RateResponseDTO> response =
        restClient
            .get()
            .uri("/rest/rates/{base}/{quote}", baseCurrency, quoteCurrency)
            .retrieve()
            .toEntity(RateResponseDTO.class);

    int statusCode = response.getStatusCode().value();
    metrics.recordLatency(sample, "rate", statusCode);
    metrics.recordCall("rate", statusCode);

    return response;
  }

  public ResponseEntity<List<RateResponseDTO>> fetchAllRates() {
    Timer.Sample sample = metrics.startSample();
    ResponseEntity<List<RateResponseDTO>> response =
        restClient
            .get()
            .uri("/rest/rates")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<RateResponseDTO>>() {});

    int statusCode = response.getStatusCode().value();
    metrics.recordLatency(sample, "all_rates", statusCode);
    metrics.recordCall("all_rates", statusCode);

    return response;
  }
}
