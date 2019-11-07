package com.github.nut077.springninja.component;

import com.github.nut077.springninja.component.property.DateTimeFormatProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class LocalDateConverter implements Converter<String, LocalDate> {

  private final DateTimeFormatProperty props;

  @Override
  public LocalDate convert(String source) {
    return LocalDate.parse(source, DateTimeFormatter.ofPattern(props.getDate()));
  }
}
