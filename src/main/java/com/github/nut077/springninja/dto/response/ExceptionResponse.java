package com.github.nut077.springninja.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ExceptionResponse {

  private String code;
  private String message;
  private OffsetDateTime timestamp = OffsetDateTime.now();
}
