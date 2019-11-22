package com.github.nut077.springninja.config;

import com.github.nut077.springninja.controller.interceptor.AccessTokenInterceptor;
import com.github.nut077.springninja.controller.interceptor.HelloInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class WebMvcConfig implements WebMvcConfigurer {

  private final HelloInterceptor helloInterceptor;
  private final AccessTokenInterceptor accessTokenInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(helloInterceptor).order(2);
    registry.addInterceptor(accessTokenInterceptor).order(1);  // order น้อยสุดจะทำลำดับแรก จะไม่ใส่ก็ได้
  }
}
