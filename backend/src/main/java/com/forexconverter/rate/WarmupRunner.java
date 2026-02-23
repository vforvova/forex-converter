package com.forexconverter.rate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "forex.cache.warmup.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class WarmupRunner implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(WarmupRunner.class);

  private final SwopProvider swopProvider;

  public WarmupRunner(SwopProvider swopProvider) {
    this.swopProvider = swopProvider;
  }

  @Override
  @Async
  public void run(ApplicationArguments args) {
    log.info("Starting cache warmup");
    swopProvider.warmupCache();
  }

  @Scheduled(cron = "0 0 1 * * *", zone = "UTC")
  public void refreshCache() {
    log.info("Starting scheduled cache refresh");
    swopProvider.warmupCache();
  }
}
