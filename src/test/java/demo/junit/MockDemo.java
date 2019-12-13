package demo.junit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@DisplayName("Mockito demo")
@ExtendWith(MockitoExtension.class)
public class MockDemo {

  @Mock
  List<Integer> mock;

  @Spy
  List<Integer> spy = new ArrayList<>();

  @Test
  void mock_demo() {
    System.out.println("mock_demo");

    // given == mock
    given(mock.size()).willReturn(3);

    // when == perform the test
    int size = mock.size();

    // then == assert
    then(mock).should().size(); // ทำการเช็คว่ามีการเรียก method size() หรือไม่
    then(mock).should(never()).clear(); // ทำการเช็คว่าไม่มีการเรียก method clear()
    assertEquals(3, size); // เช็คว่า size ต้องเท่ากับ 3
  }

  @Test
  void spy_demo() {
    System.out.println("spy_demo");

    // given
    spy.add(1);
    spy.add(2);
    spy.add(3);

    // when
    int size = spy.size();
    System.out.println("size : " + size);

    // then
    then(spy).should().add(2);
    then(spy).should(times(3)).add(anyInt());
    assertEquals(3, size);
  }

  @Test
  void spy_vs_mock_demo() {
    System.out.println("spy_vs_mock_demo");
    System.out.println("added 1 to list");

    mock.add(1);
    spy.add(1);

    System.out.println("mock size : " + mock.size()); // 0 ค่า mock จะไม่สามารถใส่ค่าเข้าไปตรงๆได้ นอกจากการ given
    System.out.println("spy size : " + spy.size()); // 1 spy คือการเรียกใช้งานตริงๆ

    System.out.println("given size = 8");
    given(mock.size()).willReturn(8);
    given(spy.size()).willReturn(8);

    System.out.println("mock size : " + mock.size());
    System.out.println("spy size : " + spy.size());
  }

  @Test
  void spy_void_demo() {
    System.out.println("spy_void_demo");

    // given
    spy.add(1);
    System.out.println("spy size : " + spy.size()); // 1

    willDoNothing().given(spy).clear();  // จะต้องไม่ทำการ clear เมื่อมีการใช้ willDoNothing

    // when
    System.out.println("clear");
    spy.clear(); // clear ตรงนี้จะไม่ถูกเคลียร์

    // then
    then(spy).should().clear();
    System.out.println("spy size : " + spy.size()); // 1
  }

  @Test
  void mock_should_in_order_demo() {
    System.out.println("mock_should_in_order_demo");

    // given
    // no given

    // when
    mock.add(5);
    mock.add(3);
    mock.add(1);

    // then
    // without InOrder
    // ถ้าไม่ใช้ InOrder เวลา test สามารถ test อะไรก่อนหลังก็ได้
    then(mock).should().add(3);
    then(mock).should().add(5);
    then(mock).should().add(1);

    // with InOrder
    // ถ้าใช้ InOrder เวลา test ต้องใส่ลำดับก่อนหลังให้ถูกต้องทุกตัว
    InOrder order = inOrder(mock);
    then(mock).should(order).add(5);
    then(mock).should(order).add(1);
    then(mock).should(order).add(3);
  }

  @Test
  void mock_exception_demo() {
    System.out.println("mock_exception_demo");

    // given
    willThrow(new RuntimeException("Mock Exception")).given(mock).get(anyInt());

    // when
    Throwable throwable = assertThrows(RuntimeException.class, () -> mock.get(1));

    // then
    assertEquals(RuntimeException.class, throwable.getClass());
    assertEquals("Mock Exception", throwable.getMessage());
    System.out.println(throwable.getClass());
    System.out.println(throwable.getMessage());
  }

  @Test
  void mock_chain() {
    System.out.println("mock_chain");

    // given
    willDoNothing().willThrow(RuntimeException.class)
      .given(mock)
      .clear();

    // when
    mock.clear();
    Throwable throwable = assertThrows(RuntimeException.class, () -> mock.clear());

    // then
    then(mock).should(times(2)).clear();
    assertEquals(RuntimeException.class, throwable.getClass());
  }
}
