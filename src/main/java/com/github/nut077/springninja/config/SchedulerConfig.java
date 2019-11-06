package com.github.nut077.springninja.config;

import com.github.nut077.springninja.component.property.SchedulerProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration(proxyBeanMethods = false)
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig implements SchedulingConfigurer {

  private final SchedulerProperty props;

  @Override
  public void configureTasks(ScheduledTaskRegistrar task) {
    ThreadPoolTaskScheduler scheduler = new TaskSchedulerBuilder()
      .poolSize(props.getPoolSize()) // ในตัวอย่างมี 3 Thread ก็เลยใส่ไว้ 3
      .threadNamePrefix(props.getThreadNamePrefix()).build();
    scheduler.initialize();
    task.setScheduler(scheduler);
  }
}
