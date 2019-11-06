package com.github.nut077.springninja.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import static java.util.stream.Stream.of;

@Configuration(proxyBeanMethods = false)
public class LazyBeanConfig implements BeanFactoryPostProcessor {

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) {
    of(factory.getBeanDefinitionNames())
      .forEach(name -> factory.getBeanDefinition(name).setLazyInit(true));
  }
}
