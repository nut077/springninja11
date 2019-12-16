package com.github.nut077.springninja.repository;

import com.github.nut077.springninja.entity.Product;
import demo.StopWatchExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest // ทำการ auto config ต่างๆ จะต่อกับ h2 in memory จะไม่กระทบกับงานจริงของเรา
@ExtendWith(StopWatchExtension.class)
@Execution(ExecutionMode.CONCURRENT) // run เป็น parallel
@DisplayName("Test product repository")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // จะทำการต่อกับ database จริงๆ
class ProductRepositoryTest {

  @Autowired private ProductRepository productRepository;

  @Test
  @DisplayName("Should_success_when_find_all_by_status_Approved")
  void findAllByStatusApproved() {
    System.out.println(String.format("[%s] findAllByStatusApproved", Thread.currentThread().getName()));

    // given
    Product p = Product
      .builder()
      .name("Freedom")
      .price(99.0)
      .status(Product.Status.APPROVED)
      .build();
    productRepository.save(p);

    // when
    List<Product> actual = productRepository.findAllByStatus(Product.Status.APPROVED);

    // then
    assertThat(actual, hasSize(1));
    assertThat(actual.get(0), hasProperty("name", is("Freedom")));
    assertThat(actual.get(0), hasProperty("price", is(99.0)));
  }

  @Test
  @DisplayName("Should_success_when_find_all_by_status_Pending")
  void findAllByStatusPending() {
    System.out.println(String.format("[%s] findAllByStatusPending", Thread.currentThread().getName()));

    // given
    Product p = Product
      .builder()
      .name("Freedom")
      .price(99.0)
      .status(Product.Status.APPROVED)
      .build();
    productRepository.save(p);

    // when
    List<Product> actual = productRepository.findAllByStatus(Product.Status.PENDING);

    // then
    assertThat(actual, hasSize(0));
    assertThat(actual, empty());
  }

  @Test
  @DisplayName("Should_success_when_updateScore")
  void updateScore() {
    System.out.println(String.format("[%s] updateScore", Thread.currentThread().getName()));

    // given
    Product p = Product
      .builder()
      .name("Freedom")
      .price(99.0)
      .status(Product.Status.APPROVED)
      .build();
    productRepository.save(p);

    // when
    int rowUpdated = productRepository.updateScore(1L, 77);
    Product actual = productRepository.findById(p.getId()).orElseThrow();

    // then
    assertThat(rowUpdated, is(1));
    assertThat(actual, hasProperty("score", is(77.0)));
  }
}