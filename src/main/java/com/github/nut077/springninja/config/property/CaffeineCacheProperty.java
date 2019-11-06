package com.github.nut077.springninja.config.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Getter
@Validated
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties("custom.caffeine.cache")
public class CaffeineCacheProperty {

  @NotNull
  @Min(1)
  private final Integer productsMaxSize;

  @NotNull
  @Min(1)
  private final Integer productMaxSize;

  @NotNull
  private final Duration expireAfterAccess;

  @NotNull
  private final Duration expireAfterWrite;

}
