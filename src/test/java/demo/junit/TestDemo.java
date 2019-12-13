package demo.junit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.TimeUnit;

@Execution(ExecutionMode.CONCURRENT) // run แบบ multiple thread โดยมี config อยู่ใน file junit-platform.properties ที่อยู่ใน resources ของ folder test
@DisplayName("Tests demo using Junit")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // จะใช้ @BeforeAll กับ @AfterAll ได้ต้องใช้ annotation นี้ด้วย
public class TestDemo implements CommonTest {

  @Test
  @DisplayName("Should_success_when_product_is_valid")
  void test_method1(TestInfo info) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(500);
    System.out.println(String.format("[%s] Execute [%s]", Thread.currentThread().getName(), info.getDisplayName()));
  }

  @Test
  @Disabled
  @DisplayName("Should_success_when_product_is_valid")
  void test_method2(TestInfo info) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(500);
    System.out.println(String.format("[%s] Execute [%s]", Thread.currentThread().getName(), info.getDisplayName()));
  }

  @Test
  @EnabledOnOs(OS.WINDOWS) // ทำเฉพาะเครื่องที่เป็น windows
  @EnabledOnJre(JRE.JAVA_11) // ทำเฉพาะ java 11
  void test_method3(TestInfo info) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(500);
    System.out.println(String.format("[%s] Execute [%s]", Thread.currentThread().getName(), info.getDisplayName()));
  }
}
