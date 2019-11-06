package com.github.nut077.springninja.service.testprofile;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("uat")
public class Minus implements Calculate {

  @Override
  public int calculate(int a, int b) {
    return a - b;
  }
}
