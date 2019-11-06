package com.github.nut077.springninja.config.testbean;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class CalculatorBeanConfig {

  @Bean
  @Qualifier("methodPlus")
  public CalculatorBean methodPlus() {
    return Double::sum;
  }

  @Bean
  @Primary
  public CalculatorBean methodMinus() {
    return (a, b) -> a - b;
  }
}
