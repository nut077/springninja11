package com.github.nut077.springninja.config;

import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class UndertowConfig {

  @Bean
  UndertowServletWebServerFactory embeddedServletContainerFactory() {
    UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
    factory.addBuilderCustomizers(
      builder ->
        builder
          .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
          .setServerOption(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH, true)
          .setIoThreads(Runtime.getRuntime().availableProcessors() * 2)
          .setWorkerThreads(((Runtime.getRuntime().availableProcessors() * 2) * 10))
          .setBufferSize(
            (1024 * 16)
              - 20) // the 20 is to allow some space for protocol headers, see
    );
    return factory;
  }

}
