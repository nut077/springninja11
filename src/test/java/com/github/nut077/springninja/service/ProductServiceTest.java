package com.github.nut077.springninja.service;

import com.github.nut077.springninja.dto.ProductDto;
import com.github.nut077.springninja.dto.mapper.ProductMapper;
import com.github.nut077.springninja.dto.mapper.ProductMapperImpl;
import com.github.nut077.springninja.dto.mapper.SetMapper;
import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.repository.ProductRepository;
import demo.StopWatchExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.support.RetryTemplate;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;


@Execution(ExecutionMode.CONCURRENT)
@ExtendWith({StopWatchExtension.class, MockitoExtension.class})
@DisplayName("Test product service")
class ProductServiceTest {

  private final SetMapper setMapper = Mappers.getMapper(SetMapper.class);
  private ProductService service;
  private ProductMapper mapper;

  @Mock
  private RetryTemplate retryTemplate;

  @Mock
  private ProductRepository productRepository;

  @BeforeEach
  void beforeEach() {
    mapper = new ProductMapperImpl(setMapper);
    service = new ProductService(productRepository, retryTemplate, mapper);
  }

  private Product gen(long id, String code, String name, double score, Product.Status status) {
    return Product
      .builder()
      .id(id)
      .code(code)
      .name(name)
      .score(score)
      .status(status)
      .build();
  }

  @Test
  void should_success_to_find_all_when_status_is_null() {
    // given
    Product p1 = gen(1, "P100", "product-1", 10.0, Product.Status.APPROVED);
    Product p2 = gen(2, "P200", "product-2", 20.0, Product.Status.PENDING);
    List<Product> products = Arrays.asList(p1, p2);
    products.sort(Comparator.comparing(Product::getId));
    given(productRepository.findAll()).willReturn(products);

    // when
    List<ProductDto> actual = service.findAll(null);

    // then
    then(productRepository).should(times(1)).findAll();
    assertThat(actual, hasSize(2));
    assertThat(actual.get(0), hasProperty("code", is("P100")));
    assertThat(actual.get(0), hasProperty("name", is("product-1")));
    assertThat(actual.get(1), hasProperty("code", is("P200")));
    assertThat(actual.get(1), hasProperty("name", is("product-2")));
  }

  @Test
  void should_success_to_find_all_when_status_approve() {
    // given
    Product p1 = gen(1, "P100", "product-1", 10.0, Product.Status.APPROVED);
    given(productRepository.findAllByStatus(Product.Status.APPROVED)).willReturn(Collections.singletonList(p1));

    // when
    List<ProductDto> actual = service.findAll(Product.Status.APPROVED);

    // then
    assertThat(actual, hasSize(1));
    assertThat(actual.get(0), hasProperty("code", is("P100")));
    assertThat(actual.get(0), hasProperty("status", is("APPROVED")));
  }

  @Test
  void should_success_to_find_when_id_is_exist() {
    // given
    Product p1 = gen(1, "P100", "product-1", 10.0, Product.Status.APPROVED);
    given(productRepository.findById(1L)).willReturn(Optional.of(p1));

    // when
    ProductDto actual = service.find(1L);

    // then
    assertThat(actual, hasProperty("code", is("P100")));
    assertThat(actual, hasProperty("name", is("product-1")));
  }

  @Test
  void should_success_to_save_when_assign_valid_value() {
    // given
    ProductDto dto = new ProductDto();
    dto.setCode("P100");
    dto.setName("product-1");
    dto.setScore(40d);
    dto.setStatus("PENDING");
    given(productRepository.save(any(Product.class))).willReturn(mapper.map(dto));

    // when
    ProductDto actual = service.save(dto);

    // then
    assertThat(actual, hasProperty("code", is("P100")));
    assertThat(actual, hasProperty("score", is(40.0)));
  }

  @Test
  void should_success_to_replace_when_assign_valid_value() {
    // given
    ProductDto dto = new ProductDto();
    dto.setCode("P100");
    dto.setName("product-1");
    dto.setScore(40d);
    dto.setStatus("PENDING");

    Product entity = mapper.map(dto);
    entity.setId(5L);

    given(productRepository.findById(5L)).willReturn(Optional.of(entity));
    given(productRepository.save(any(Product.class))).willReturn(entity);

    // when
    ProductDto actual = service.replace(5L, dto);

    // then
    assertThat(actual, hasProperty("code", is("P100")));
    assertThat(actual, hasProperty("status", is("PENDING")));
  }

  @Test
  void should_success_to_updated_score_when_id_is_exist() {
    // given
    given(productRepository.updateScore(anyLong(), anyDouble())).willReturn(1);

    // when
    int actual = service.updateScore(1L, 100);

    // then
    assertThat(actual, is(1));
  }

  @Test
  void should_success_to_update_score_when_id_is_not_exist() {
    // given
    given(productRepository.updateScore(anyLong(), anyDouble())).willReturn(0);

    // when
    int actaul = service.updateScore(1L, 100);

    // then
    assertThat(actaul, is(0));
  }

  @Test
  void should_success_to_dalete_any_id() {
    // given
    willDoNothing().given(productRepository).deleteById(anyLong());

    // when
    service.delete(1L);

    // then
    then(productRepository).should(times(1)).deleteById(anyLong());
    then(productRepository).should(never()).deleteAll();
  }
}