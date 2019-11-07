package com.github.nut077.springninja.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.nut077.springninja.component.property.DateTimeFormatProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class JacksonConfig {

  private final DateTimeFormatProperty props;

  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(props.getDateTime());
    JavaTimeModule timeModule = new JavaTimeModule();
    timeModule.addSerializer(
      OffsetDateTime.class,
      new JsonSerializer<>() {
        @Override
        public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
          jsonGenerator.writeString(dateTimeFormatter.format(offsetDateTime));
        }
      }
    );
    return Jackson2ObjectMapperBuilder.json()
      .modules(timeModule)
      //.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE) // style name json
      .serializationInclusion(JsonInclude.Include.NON_EMPTY) // ไม่เอาค่าที่เป็น null หรือ empty ออกมา
      .featuresToEnable(SerializationFeature.INDENT_OUTPUT) // จัดรูปแบบ json
      .featuresToDisable(
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        SerializationFeature.FAIL_ON_EMPTY_BEANS,
        DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
        DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
      );
  }
}
