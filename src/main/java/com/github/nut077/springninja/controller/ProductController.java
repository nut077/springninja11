package com.github.nut077.springninja.controller;

import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
public class ProductController extends CommonController {

  private final ProductService productService;

  @GetMapping("/products")
  public ResponseEntity<List<Product>> getAll() {
    log.info(() -> "ProductController :: getAll");
    return ResponseEntity.ok(productService.findAll());
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<Product> get(@PathVariable Long id) {
    log.info(() -> "ProductController :: get");
    return ResponseEntity.ok(productService.find(id));
  }

  @PostMapping("/products")
  public ResponseEntity<Product> save(@RequestBody Product product) {
    log.info(() -> "ProductController :: save");
    return ResponseEntity.ok(productService.save(product));
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<Product> replace(@PathVariable Long id, @RequestBody Product product) {
    log.info(() -> "ProductController :: replace");
    return ResponseEntity.ok(productService.replace(id, product));
  }

  @PatchMapping("/products/{id}/{score}")
  public int updateScore(@PathVariable Long id, @PathVariable double score) {
    log.info(() -> "ProductController :: updateScore");
    return productService.updateScore(id, score);
  }

  @DeleteMapping("/products/{id}")
  public void delete(@PathVariable Long id) {
    log.info(() -> "ProductController :: delete");
    productService.delete(id);
  }

}