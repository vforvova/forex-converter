package com.forexconverter.rate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

class CacheConfigTest {

  @Test
  void shouldDefineExchangeRatesCache() {
    CacheConfig cacheConfig = new CacheConfig();
    CacheManager cacheManager = cacheConfig.cacheManager();

    assertNotNull(cacheManager);
    assertTrue(cacheManager.getCacheNames().contains("exchangeRates"));
  }
}
