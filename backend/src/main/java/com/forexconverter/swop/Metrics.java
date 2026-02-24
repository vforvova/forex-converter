package com.forexconverter.swop;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class Metrics {

  private final MeterRegistry registry;

  public Metrics(MeterRegistry registry) {
    this.registry = registry;
  }

  public Timer.Sample startSample() {
    return Timer.start(registry);
  }

  public void recordLatency(Timer.Sample sample, String method, int statusCode) {
    sample.stop(
        Timer.builder("swop.api.latency")
            .description("SWOP API call latency")
            .tag("method", method)
            .tag("status", String.valueOf(statusCode))
            .publishPercentiles(0.95, 0.99, 0.999)
            .register(registry));
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
