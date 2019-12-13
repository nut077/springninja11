package demo.junit;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DynamicDemo implements CommonTest {

  @TestFactory
  Collection<DynamicTest> dynamicTestFromCollection() {
    // จำทำการ test ทั้งหมด 4 ครั้ง
    List<Integer> list = Arrays.asList(2, 4, 6, 8);
    List<DynamicTest> results = new ArrayList<>();
    list.forEach(i -> results.add(dynamicTest("Should_success_when_mod2_equals_zero_" + i,
      () -> assertTrue(i % 2 == 0, "Failure message"))));
    return results;
  }

  @TestFactory
  Stream<DynamicTest> dynamicTestStream() {
    Stream<String> stream = Stream.of("Apple", "Lemon", "Coconut");
    return stream.map(name -> dynamicTest("Should_success_when_name_length_more_than_4 : " + name,
      () -> assertTrue(name.length() >= 5)));
  }

  @TestFactory
  Stream<DynamicNode> dynamicTestWithContainers() {
    IntStream stream = IntStream.of(500, 101);
    return stream.mapToObj(price -> dynamicContainer("Container_for_tests_" + price, Stream.of(
      dynamicTest("not null", () -> assertNotNull(price)),
      dynamicContainer("check amount", Stream.of(
        dynamicTest("price >= 100", () -> assertTrue(price >= 100)),
        dynamicTest("price <= 1000", () -> assertTrue(price <= 1000))
      ))
    )));
  }
}
