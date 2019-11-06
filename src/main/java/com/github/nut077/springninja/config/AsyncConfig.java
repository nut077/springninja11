package com.github.nut077.springninja.config;

import com.github.nut077.springninja.component.property.AsyncProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration(proxyBeanMethods = false)
@Log4j2
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

  private final AsyncProperty props;

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor =
      new TaskExecutorBuilder()
        .corePoolSize(props.getCorePoolSize())
        .queueCapacity(props.getQueueCapacity())
        .maxPoolSize(props.getMaxPoolSize())
        .threadNamePrefix(props.getThreadNamePrefix())
        .keepAlive(props.getKeepAlive())
        .allowCoreThreadTimeOut(props.getAllowCoreThreadTimeOut())
        .taskDecorator(runnable -> process(runnable, ThreadContext.getContext()))
        .build();
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.initialize();
    return executor;
  }

  private Runnable process(Runnable runnable, Map<String, String> context) {
    return () -> {
      ThreadContext.putAll(context);
      runnable.run();
      ThreadContext.clearAll();
    };
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (ex, method, params) -> {
      log.error("AsyncMethod :: {}({})", method.getName(), params);
      log.error("Exception :: {}", ex.getMessage());
      // save exception information to queue or database for retry
    };
  }
}
