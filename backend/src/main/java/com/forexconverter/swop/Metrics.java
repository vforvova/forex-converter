package com.forexconverter.swop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class Metrics {

  private final MeterRegistry registry;

  public Metrics(MeterRegistry registry) {
    this.registry = registry;
  }

  public void recordCall(String method, int statusCode) {
    Counter.builder("swop.api.calls")
        .description("Number of SWOP API calls")
        .tag("method", method)
        .tag("status_code", String.valueOf(statusCode))
        .register(registry)
        .increment();
  }
}
