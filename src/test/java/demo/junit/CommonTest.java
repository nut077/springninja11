package demo.junit;

import demo.StopWatchExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(StopWatchExtension.class) // ใช้ความสามารถของ class นี้ คือการจับเวลา
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // จะใช้ @BeforeAll กับ @AfterAll ได้ต้องใช้ annotation นี้ด้วย
public interface CommonTest {

  @BeforeAll
    // จะทำครั้งเดียวก่อนการ run test
  default void beforeAllTests() {
    System.out.println(String.format("[%s] Before all tests", Thread.currentThread().getName()));
  }

  @AfterAll
    // จะทำครั้งเดียวหลังจากสิ้นสุดการ run test
  default void afterAllTests() {
    System.out.println(String.format("[%s] After all tests", Thread.currentThread().getName()));
  }

  @BeforeEach
    // จะทำก่อนทุกๆ method ที่ run test
  default void beforeEachTest(TestInfo info) {
    System.out.println(String.format("[%s] Before execute [%s]", Thread.currentThread().getName(), info.getDisplayName()));
  }

  @AfterEach
    // จะทำหลังทุกๆ method ที่ run test
  default void afterEachTest(TestInfo info) {
    System.out.println(String.format("[%s] Finished executing [%s]", Thread.currentThread().getName(), info.getDisplayName()));
  }
}
