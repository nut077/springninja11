package demo.junit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

public class AssumptionDemo implements CommonTest {

  @Test
  void trueAssumption() {
    assumeTrue(5 > 4); // ถ้าเป็น false ก็จะไม่ทำบรรทัดถัดไป แต่จะไม่แสดงว่า test ไม่ผ่าน
    assertEquals(5 + 5, 10);
  }

  @Test
  void assumptionThat() {
    boolean isMobile = true;
    int mobileVersion = 2;
    String appVersion = "1.2.3";

    assumingThat(
      isMobile, // ถ้าเป็น true assertAll ต่อ แต่ถ้าเป็น false จะไม่เข้าไป test ที่ assertAll แต่ test จะผ่าน ต่อให้ข้างใน assertAll มีค่าเป็น false ก็ตาม
      () -> assertAll(
        () -> assertEquals(3, mobileVersion),
        () -> assertEquals("1.2.3", appVersion)
      )
    );
  }
}
