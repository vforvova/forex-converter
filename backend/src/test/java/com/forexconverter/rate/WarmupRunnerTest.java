package com.forexconverter.rate;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class WarmupRunnerTest {

  @Mock private SwopProvider swopProvider;
  @Mock private ApplicationArguments args;

  private WarmupRunner runner;

  @BeforeEach
  void setUp() {
    runner = new WarmupRunner(swopProvider);
  }

  @DisplayName("Should trigger cache warmup on application startup")
  @Test
  void shouldTriggerWarmupOnStartup() {
    runner.run(args);

    verify(swopProvider).warmupCache();
  }
}
