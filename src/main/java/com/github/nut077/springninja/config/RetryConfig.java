package com.github.nut077.springninja.config;

import com.github.nut077.springninja.component.property.RetryProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRetry
@RequiredArgsConstructor
public class RetryConfig {

  private final RetryProperty props;

  @Bean
  public RetryTemplate build() {
    RetryTemplate template = new RetryTemplate();
    Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
    exceptions.put(RuntimeException.class, true);
    exceptions.put(Exception.class, false);
    template.setRetryPolicy(new SimpleRetryPolicy(props.getMaxAttempts(), exceptions));  // ค่า default เท่ากับ 3 คือ จะ retry อีก 2 ครั้ง เพราะครั้งแรกที่เรียก method นี้ maxAttempts จะนับเป็น 1 retry ครั้งแรกนับเป็น 2 ครั้งต่อมานับเป็น 3

    ExponentialBackOffPolicy exponential = new ExponentialBackOffPolicy();
    exponential.setMultiplier(props.getMultiplier()); // 2 -->> retry ครั้งแรกเริ่ม 1 วิ ครั้งต่อมา คือ 1000 * 2 == 2 วิ ครั้งต่อมา 2000 * 2 == 4 วิ
    exponential.setInitialInterval(props.getInitialInterval()); // 1000 -->> retry ครั้งแรกเริ่ม 1 วิ ครั้งต่อมา คือ 1000 * 2 == 2 วิ ครั้งต่อมา 2000 * 2 == 4 วิ
    exponential.setMaxInterval(props.getMaxInterval()); // เวลาสูงสุดที่จะ ไม่ retry
    template.setBackOffPolicy(exponential);

    return template;
  }
}
