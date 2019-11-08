package com.github.nut077.springninja.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class SuccessResponse<T> {

  private String code;
  private String message;
  private OffsetDateTime timestamp;
  private T data;

  private SuccessResponse(String code, String message, OffsetDateTime timestamp, T data) {
    this.code = code;
    this.message = message;
    this.timestamp = timestamp;
    this.data = data;
  }

  public static class SuccessResponseBuilder<T> {

    private String code;
    private String message;
    private OffsetDateTime timestamp;
    private T data;

    SuccessResponseBuilder() {}

    SuccessResponseBuilder<T> code(String code) {
      this.code = code;
      return this;
    }

    public SuccessResponseBuilder<T> message(String message) {
      this.message = message;
      return this;
    }

    SuccessResponseBuilder<T> timestamp(OffsetDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    SuccessResponseBuilder<T> data(T data) {
      this.data = data;
      return this;
    }

    public SuccessResponse<T> build() {
      return new SuccessResponse<>(code, message, timestamp, data);
    }

    public String toString() {
      return "SuccessResponse.SuccessResponseBuilder(code="
        + this.code
        + ", message="
        + this.message
        + ", timestamp="
        + this.timestamp
        + ", data="
        + this.data
        + ")";
    }
  }
}
