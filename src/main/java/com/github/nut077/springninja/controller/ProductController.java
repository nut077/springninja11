package com.github.nut077.springninja.controller;

import com.github.nut077.springninja.dto.ProductDto;
import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.github.nut077.springninja.dto.response.SuccessResponse.builder;
import static org.springframework.http.ResponseEntity.ok;

@Log4j2
@RestController
@RequiredArgsConstructor
public class ProductController extends CommonController {

  private final ProductService productService;

  @GetMapping("/products")
  public ResponseEntity getAll(@RequestParam(required = false) Product.Status status) {
    log.info(() -> "ProductController :: getAll");
    return ok(builder(productService.findAll(status)).build());
  }

  @GetMapping("/products/{id}")
  public ResponseEntity get(@PathVariable Long id) {
    log.info(() -> "ProductController :: get");
    return ok(builder(productService.find(id)).build());
  }

  @PostMapping("/products")
  public ResponseEntity save(@RequestBody ProductDto dto) {
    log.info(() -> "ProductController :: save");
    return ok(builder(productService.save(dto)).message("created").build());
  }

  @PutMapping("/products/{id}")
  public ResponseEntity replace(@PathVariable Long id, @RequestBody ProductDto dto) {
    log.info(() -> "ProductController :: replace");
    return ok(builder(productService.replace(id, dto)).message("product updated").build());
  }

  @PatchMapping("/products/{id}/{score}")
  public ResponseEntity updateScore(@PathVariable Long id, @PathVariable double score) {
    log.info(() -> "ProductController :: updateScore");
    return ok(builder(productService.updateScore(id, score)).message("product score updated").build());
  }

  @DeleteMapping("/products/{id}")
  public void delete(@PathVariable Long id) {
    log.info(() -> "ProductController :: delete");
    productService.delete(id);
  }

}
