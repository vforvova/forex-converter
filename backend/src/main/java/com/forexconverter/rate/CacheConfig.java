package com.forexconverter.rate;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableCaching
@EnableAsync
public class CacheConfig {

  public static final String EXCHANGE_RATES_CACHE = "exchangeRates";

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(EXCHANGE_RATES_CACHE);
    cacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).maximumSize(1000));
    return cacheManager;
  }
}
