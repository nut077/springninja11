package demo;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.engine.TestDescriptor;

import java.time.Duration;
import java.time.LocalTime;

public class StopWatchExtension implements BeforeAllCallback, AfterAllCallback {

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    getStore(context).getOrComputeIfAbsent("begin", k -> LocalTime.now());
    TestDescriptor testDescriptor = (TestDescriptor) FieldUtils.readField(context.getRoot(), "testDescriptor", true);
    getStore(context).getOrComputeIfAbsent("testClassCount", k -> testDescriptor.getChildren().size());
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    int testClassCount = (int) getStore(context).get("testClassCount") - 1;
    if (testClassCount <= 0) {
      System.out.println(">> Execution Time = " +
        Duration.between((LocalTime) getStore(context).get("begin"), LocalTime.now()).toMillis() + " ms");
    }
    getStore(context).put("testClassCount", testClassCount);
  }

  private ExtensionContext.Store getStore(ExtensionContext context) {
    return context
      .getRoot()
      .getStore(ExtensionContext.Namespace.create("com.github.nut077.springninja"));
  }
}
