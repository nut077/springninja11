package com.github.nut077.springninja.component;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class YmlSourceFactory implements PropertySourceFactory {

  @Override
  @SneakyThrows
  public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource.getResource());
    factory.afterPropertiesSet();
    return new PropertiesPropertySource(
      (name != null) ? name : resource.getResource().getFilename(),
      Objects.requireNonNull(factory.getObject()));
  }
}
