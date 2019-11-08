package com.github.nut077.springninja.controller.advice;

import com.github.nut077.springninja.dto.response.ExceptionResponse;
import com.github.nut077.springninja.exception.CommonException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@RestControllerAdvice
public class CommonAdvice extends ResponseEntityExceptionHandler {

  private ResponseEntity<Object> handle(Exception ex, HttpStatus status, String code) {
    log.error(ex);
    ExceptionResponse response = new ExceptionResponse();
    response.setCode("xxx-" + code);
    response.setMessage(ex.getMessage());
    HttpHeaders headers = new HttpHeaders();
    headers.add("x-Information", "https://domain-name/context-path/api/v1/errors/" + code);
    return ResponseEntity.status(status).headers(headers).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return handle(ex, status, String.valueOf(status.value()));
  }

  @ExceptionHandler(CommonException.class)
  protected ResponseEntity<Object> handle(CommonException ex) {
    return handle(ex, ex.getStatus(), ex.getCode());
  }

  @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
  protected ResponseEntity<Object> handleIllegal(RuntimeException ex) {
    return handle(ex, HttpStatus.BAD_REQUEST, String.valueOf(HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handle(Exception ex) {
    return handle(ex, HttpStatus.INTERNAL_SERVER_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
  }

}
