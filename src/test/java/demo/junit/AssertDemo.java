package demo.junit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Assert demo using Hamcrest")
public class AssertDemo implements CommonTest {

  @Test
  void standardAssertions() {
    assertTrue(3 > 4, "show message if condition is false");
  }

  @Test
  void groupedAssertions() {
    String productName = "Apple";
    double productScore = 55.5;

    assertAll("products",
      () -> assertEquals("Apple", productName),
      () -> assertEquals(55.5, productScore));
  }

  @Test
  void assertEqualsExceptionMessage() {
    Throwable exception = assertThrows(UnsupportedOperationException.class, () -> {
      throw new UnsupportedOperationException("Not supported");
    });
    assertEquals("Not supported", exception.getMessage());
  }

  @Test
  void assertThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> Integer.parseInt("a"), "Exception");
  }

  @Test
  void timeoutExecuted() {
    // execution exceeded timeout of 10 ms by 21 ms
    // ถ้า process ใช้เวลานานกว่าที่กำหนดไว้จะ test ไม่ผ่าน และบอกเวลาที่เกินไป อย่างเช่น ต้องการใช้เวลาแค่ 10 ms แต่ใช้จริง เกินมา 21 ms แสดงว่า process ใช้เวลาทั้งหมด 31 ms
    assertTimeout(ofMillis(10), () -> Thread.sleep(30));
  }

  @Test
  void timeoutExecutedWithPreemtiveTermination() {
    // จะเหมือนกับ assertTimeout แต่จะแตกต่างกันแค่  message -->> execution timed out after 10 ms คือไม่บอกว่าใช้เกินมาเท่าไร
    assertTimeoutPreemptively(ofMillis(10), () -> Thread.sleep(30));
  }
}
