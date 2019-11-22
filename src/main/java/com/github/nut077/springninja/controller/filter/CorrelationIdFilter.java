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
import java.io.IOException;
import java.util.UUID;

@Log4j2
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  private static final String X_CORRELATION_KEY = "x-Correletion-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
    if (StringUtils.countOccurrencesOf(request.getServletPath(), "/api/v") > 0) {
      log.info("doFilterInternal");

      String correlationId = "xxx-" +
        (!StringUtils.isEmpty(request.getHeader(X_CORRELATION_KEY))
          ? request.getHeader(X_CORRELATION_KEY)
          : UUID.randomUUID().toString().toLowerCase().substring(0, 8));

      // set correlation-id to response header
      response.setHeader(X_CORRELATION_KEY, correlationId);

      // set correlation-id to log4j2
      ThreadContext.put("c-id", correlationId); // c-id map กับ value: "%highlight{[%-5p]} %d %X{c-id} %c{1} > %m%n%ex{full}" ใน log4j2.yml

      // pass to Spring MVC dispatcher
      filterChain.doFilter(request, response);

      // release all resources
      ThreadContext.clearAll();
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
