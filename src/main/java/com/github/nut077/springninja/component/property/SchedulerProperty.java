package com.github.nut077.springninja.component.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties("custom.scheduler")
public class SchedulerProperty {

  private final Integer poolSize;
  private final String threadNamePrefix;
}
