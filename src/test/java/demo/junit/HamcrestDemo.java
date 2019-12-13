package demo.junit;

import com.github.nut077.springninja.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Test with Hamcrest")
public class HamcrestDemo implements CommonTest {

  @Nested
  @DisplayName("Assert Objects")
  class AssertObjects {

    @Test
    @DisplayName("Should be null")
    void shouldBeNull() {
      assertThat(null, nullValue());
    }

    @Test
    @DisplayName("Should not be null")
    void shouldNotBeNull() {
      assertThat(new Object(), notNullValue());
    }
  }

  @Nested
  @DisplayName("Assert String")
  class AssertString {

    @Test
    @DisplayName("Should be empty string")
    void shouldBeEmptyString() {
      String str = "";
      assertThat(str, emptyString());
    }

    @Test
    @DisplayName("Should be empty or null string")
    void shouldBeEmptyOfNullString() {
      String str = null;
      assertThat(str, emptyOrNullString());
    }

    @Test
    @DisplayName("Should be equal ignore case")
    void shouldBeEqualIgnoreCase() {
      String a = "apple";
      String b = "APPLE";
      assertThat(a, equalToIgnoringCase(b));
    }
  }

  @Nested
  @DisplayName("Assert Integers")
  class AssertIntegers {

    @Test
    @DisplayName("Should be equal")
    void shouldBeEqual() {
      assertThat(4, is(4));
    }

    @Test
    @DisplayName("Should be not equal")
    void shouldBeNotEqual() {
      assertThat(4, not(5));
    }

    @Test
    @DisplayName("Should correct value")
    void shouldCorrectValue() {
      assertThat(1.5, lessThan(2.0));
      assertThat(1.5, lessThanOrEqualTo(2.0));
    }
  }

  @Nested
  @DisplayName("Assert References")
  class AssertReferences {

    @Test
    @DisplayName("Should refer to the same object")
    void shouldReferenceToSameObject() {
      Object actual = new Object();
      Object expected = actual;
      assertThat(actual, sameInstance(expected));
    }

    @Test
    @DisplayName("Should not reference to the same object")
    void shouldNotReferenceToSameObject() {
      Object actual = new Object();
      Object expeced = new Object();
      assertThat(actual, not(sameInstance(expeced)));
    }
  }

  @Nested
  @DisplayName("Assert Collection")
  class AssertCollection {

    List<Integer> list;

    @BeforeEach
    void init() {
      list = new ArrayList<>();
    }

    @Test
    @DisplayName("Should empty")
    void shouldEmpty() {
      assertThat(list, empty());
    }

    @Test
    @DisplayName("Should contain two elements")
    void shouldContainTwoElements() {
      list = Arrays.asList(1, 2);
      assertThat(list, hasSize(2));
    }

    @Test
    @DisplayName("Should contain the correct elements in the given order")
    void shouldContainCorrectElementsInGivenOrder() {
      list = Arrays.asList(3, 4);
      assertThat(list, contains(3, 4)); // ต้องเรียงตามลำดับถึงจะถูก
    }

    @Test
    @DisplayName("Should contain the correct elements in any order")
    void shouldContainCorrectElementsInAnyOrder() {
      list = Arrays.asList(5, 3, 4);
      assertThat(list, containsInAnyOrder(5, 4, 3)); // ไม่ต้องเรียงลำดับก็ได้
    }

    @Test
    @DisplayName("Should contain a correct element")
    void shouldContainCorrectElement() {
      list = Arrays.asList(5, 4, 3);
      assertThat(list, hasItem(4));
    }

    @Test
    @DisplayName("Should not contain an incorrect element")
    void shouldNotContainIncorrectElement() {
      list = Arrays.asList(5, 4, 3);
      assertThat(list, not(hasItem(1)));
    }
  }

  @Nested
  @DisplayName("Asserts Map")
  class AssertsMap {

    Map<String, Object> map;

    @BeforeEach
    void init() {
      map = new LinkedHashMap<>();
      map.put("k1", "v1");
    }

    @Test
    @DisplayName("Should contain the correct key")
    void shouldContainCorrectKey() {
      assertThat(map, hasKey("k1"));
    }

    @Test
    @DisplayName("Should not contain the incorrect key")
    void shouldNotContainIncorrectKey() {
      assertThat(map, not(hasKey("k2")));
    }

    @Test
    @DisplayName("Should contain the correct value")
    void shouldContainCorrectValue() {
      assertThat(map, hasEntry("k1", "v1"));
    }

    @Test
    @DisplayName("Combine multiple assertions")
    void shouldCorrectAsserts() {
      assertThat(map, allOf(
        hasKey("k1"),
        not(hasKey("k2")),
        hasEntry("k1", "v1")
      ));
    }
  }

  @Nested
  @DisplayName("Asserts Model")
  class AssertsModel {

    private Product product;

    @BeforeEach
    void init() {
      product = Product
        .builder()
        .code("p1")
        .name("apple")
        .price(100)
        .build();
    }

    @Test
    @DisplayName("Should have correct code and name")
    void shouldHaveCorrectCodeAndName() {
      assertThat("The product code must be 'p1' and name must be 'apple' or 'lemon'",
        product, allOf(
          hasProperty("code", is("p1")),
          anyOf(
            hasProperty("name", is("apple")),
            hasProperty("name", is("lemon"))
          )
        )
      );
    }
  }

  @Nested
  @DisplayName("Asserts REgex")
  class AssertsRegex {

    @Test
    void shouldCorrectEmailMatcher() {
      String email = "mail@mail.com";
      assertThat(email, EmailMatcher.isEmail());
    }
  }

}
