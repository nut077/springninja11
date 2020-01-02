package com.github.nut077.springninja.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nut077.springninja.controller.interceptor.AccessTokenInterceptor;
import com.github.nut077.springninja.dto.ProductDto;
import com.github.nut077.springninja.dto.mapper.ProductMapper;
import com.github.nut077.springninja.dto.mapper.ProductMapperImpl;
import com.github.nut077.springninja.dto.mapper.SetMapper;
import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.service.ProductService;
import demo.StopWatchExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Test product controller")
@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith({MockitoExtension.class, StopWatchExtension.class})
@WebMvcTest(ProductController.class)
class ProductControllerTest {

  private final ProductMapper mapper = new ProductMapperImpl(Mappers.getMapper(SetMapper.class));

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ProductService service;

  @SpyBean
  private AccessTokenInterceptor interceptor;

  // Object to JSON
  private String toJsonString(final Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  // mock product
  private Product gen(long id, String code, String name, double price, Product.Status status) {
    return gen(id, code, name, price, status, 50);
  }

  private Product gen(long id, String code, String name, double price, Product.Status status, double score) {
    return Product
      .builder()
      .id(id)
      .code(code)
      .name(name)
      .price(price)
      .status(status)
      .score(score)
      .build();
  }

  private HttpHeaders getHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("x-Access-Token", "aa");
    return headers;
  }

  @Test
  void should_success_to_find_all() throws Exception {
    // given
    List<Product> products = Arrays.asList(
      gen(1L, "P100", "Shirt", 1000, Product.Status.APPROVED),
      gen(2L, "P200", "Jean", 1500, Product.Status.DELETED)
    );
    given(service.findAll(null)).willReturn(mapper.map(products));

    // when
    mockMvc
      .perform(get("/api/v1/products").headers(getHttpHeaders()))
      .andDo(print())
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.data", hasSize(2)))
      .andExpect(jsonPath("$.data[0].code", is("P100")))
      .andExpect(jsonPath("$.data[0].name", is("Shirt")))
      .andExpect(jsonPath("$.data[1].code", is("P200")))
      .andExpect(jsonPath("$.data[1].name", is("Jean")));
  }

  @Test
  void should_success_to_find_one() throws Exception {
    // given
    given(service.find(anyLong())).willReturn(mapper.map(gen(1L, "P100", "Shirt", 1000.0, Product.Status.DELETED)));

    // when
    mockMvc
      .perform(get("/api/v1/products/1").headers(getHttpHeaders()))
      .andDo(print())
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.data.code", is("P100")))
      .andExpect(jsonPath("$.data.name", is("Shirt")));
  }

  @Test
  void should_success_to_save() throws Exception {
    // given
    ProductDto dto = mapper.map(gen(1L, "P100", "Shirt", 1000.0, Product.Status.APPROVED));
    given(service.save(any(ProductDto.class))).willReturn(dto);

    // when
    mockMvc
      .perform(post("/api/v1/products")
        //.with(csrf())
        .headers(getHttpHeaders())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(toJsonString(dto))
      )
      .andDo(print())
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.data.code", is("P100")))
      .andExpect(jsonPath("$.data.name", is("Shirt")))
      .andExpect(jsonPath("$.data.price", is(1000.0)))
      .andExpect(jsonPath("$.code", is("xxx-200")))
      .andExpect(jsonPath("$.message", is("created")));
  }

  @Test
  void should_success_to_replace() throws Exception {
    // given
    ProductDto dto = mapper.map(gen(1L, "P100", "T-Shirt", 1200.0, Product.Status.APPROVED));
    given(service.replace(anyLong(), any(ProductDto.class))).willReturn(dto);

    // when
    mockMvc
      .perform(
        put("/api/v1/products/{id}", 1L)
          //.with(csrf())
          .headers(getHttpHeaders())
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(toJsonString(dto))
      )
      .andDo(print())
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.code", is("xxx-200")))
      .andExpect(jsonPath("$.message", is("product updated")))
      .andExpect(jsonPath("$.data.name", is("T-Shirt")))
      .andExpect(jsonPath("$.data.price", is(1200.0)));
  }

  @Test
  void should_success_to_update_score() throws Exception {
    // given
    given(service.updateScore(anyLong(), anyDouble())).willReturn(1);

    // when
    mockMvc
      .perform(
        patch("/api/v1/products/{id}/{score}", 1L, 99.0)
          //.with(csrf())
          .headers(getHttpHeaders())
          .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andDo(print())
      // then
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.code", is("xxx-200")))
      .andExpect(jsonPath("$.message", is("product score updated")));
  }

  @Test
  void should_succes_to_delete() throws Exception {
    // given
    willDoNothing().given(service).delete(anyLong());

    // when
    mockMvc
      .perform(
        delete("/api/v1/products/1")
          //.with(csrf())
          .headers(getHttpHeaders())
          .contentType(MediaType.APPLICATION_JSON_VALUE)
      )
      .andDo(print())
      // then
      .andExpect(status().isOk());
  }

}