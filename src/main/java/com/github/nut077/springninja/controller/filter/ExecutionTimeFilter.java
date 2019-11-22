package com.github.nut077.springninja.controller.filter;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;

@Log4j2
@Component
public class ExecutionTimeFilter extends OncePerRequestFilter {

  private static final String X_EXECUTION_TIME_KEY = "x-Execution-Time-Millis";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (StringUtils.countOccurrencesOf(request.getServletPath(), "/api/v") > 0) {
      log.info("doFilterInternal");
      LocalTime localTime = LocalTime.now();
      filterChain.doFilter(
        request,
        new HttpServletResponseWrapper(response) {
          @Override
          public void setStatus(int sc) {
            super.setStatus(sc);
            addHeader(
              X_EXECUTION_TIME_KEY,
              String.valueOf(Duration.between(localTime, LocalTime.now()).toMillis())
            );
          }
        }
      );
      ThreadContext.clearAll();
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
