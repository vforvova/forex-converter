package com.forexconverter.swop;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class Client {

  private final RestClient restClient;

  public Client(ClientProperties properties) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(properties.timeout());
    requestFactory.setReadTimeout(properties.timeout());

    this.restClient =
        RestClient.builder()
            .baseUrl(properties.baseUrl())
            .defaultHeader("Authorization", "ApiKey " + properties.apiKey())
            .requestFactory(requestFactory)
            .build();
  }

  public RateResponseDTO fetchRate(String baseCurrency, String quoteCurrency) {
    return restClient
        .get()
        .uri("/rest/rates/{base}/{quote}", baseCurrency, quoteCurrency)
        .retrieve()
        .body(RateResponseDTO.class);
  }

  public AllRatesResponse fetchAllRates() {
    List<RateResponseDTO> rates =
        restClient
            .get()
            .uri("/rest/rates")
            .retrieve()
            .body(new ParameterizedTypeReference<List<RateResponseDTO>>() {});
    return new AllRatesResponse(rates);
  }
}
