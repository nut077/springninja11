package com.github.nut077.springninja.component.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties("custom.retry")
public class RetryProperty {

  private final Integer maxAttempts;
  private final Integer multiplier;
  private final Integer initialInterval;
  private final Integer maxInterval;
}
