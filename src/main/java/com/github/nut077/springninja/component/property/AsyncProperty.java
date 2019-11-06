package com.github.nut077.springninja.component.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Getter
@Validated
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties("custom.async")
public class AsyncProperty {

  @NotNull
  @Min(1)
  @Max(16)
  private final Integer corePoolSize;
  private final Integer queueCapacity;
  private final Integer maxPoolSize;
  private final Duration keepAlive;
  private final String threadNamePrefix;
  private final Boolean allowCoreThreadTimeOut;
}
