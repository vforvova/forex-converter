package com.forexconverter.client;

import com.forexconverter.config.SwopClientProperties;
import com.forexconverter.dto.SwopRateResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SwopClient {

  private final RestClient restClient;

  public SwopClient(SwopClientProperties properties) {
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

  public SwopRateResponse fetchRate(String baseCurrency, String quoteCurrency) {
    return restClient
        .get()
        .uri("/rest/rates/{base}/{quote}", baseCurrency, quoteCurrency)
        .retrieve()
        .body(SwopRateResponse.class);
  }
}
