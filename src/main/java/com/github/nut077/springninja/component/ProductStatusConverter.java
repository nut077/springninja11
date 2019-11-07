package com.github.nut077.springninja.component;

import com.github.nut077.springninja.entity.Product;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductStatusConverter implements Converter<String, Product.Status> {

  @Override
  public Product.Status convert(String code) {
    return Product.Status.codeToStatus(code);
  }
}
