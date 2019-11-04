package com.github.nut077.springninja.config;

import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
public class SchedulerConfig implements SchedulingConfigurer {

  @Override
  public void configureTasks(ScheduledTaskRegistrar task) {
    ThreadPoolTaskScheduler scheduler = new TaskSchedulerBuilder()
      .poolSize(3) // ในตัวอย่างมี 3 Thread ก็เลยใส่ไว้ 3
      .threadNamePrefix("Scheduler-").build();
    scheduler.initialize();
    task.setScheduler(scheduler);
  }
}
