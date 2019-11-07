package com.github.nut077.springninja.component.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties("custom.formatdatetime")
public class DateTimeFormatProperty {

  private final String date;
  private final String dateTime;
}
