package com.github.nut077.springninja.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class UnauthorizedException extends CommonException {

  public UnauthorizedException(String message) {
    super(HttpStatus.UNAUTHORIZED, String.valueOf(HttpStatus.UNAUTHORIZED), message);
  }
}
