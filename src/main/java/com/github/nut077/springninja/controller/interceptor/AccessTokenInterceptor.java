package com.github.nut077.springninja.controller.interceptor;

import com.github.nut077.springninja.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class AccessTokenInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info("preHandle");
    return validate(request.getHeader("x-Access-Token"));
  }

  private boolean validate(String accessToken) {
    List<String> list = Arrays.asList("aa", "bb");
    if (!list.contains(accessToken)) {
      throw new UnauthorizedException("Invalid access-token");
    }
    return true;
  }
}
