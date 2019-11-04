package com.github.nut077.springninja.service;

import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.github.nut077.springninja.config.CaffeineCacheConfig.CacheName.PRODUCT;
import static com.github.nut077.springninja.config.CaffeineCacheConfig.CacheName.PRODUCTS;

@Service
@Log4j2
@RequiredArgsConstructor
@CacheConfig(cacheNames = PRODUCT) // cache name default
public class ProductService {

  private final ProductRepository productRepository;

  @Cacheable(cacheNames = PRODUCTS) // cache name is PRODUCTS
  public List<Product> findAll() {
    log.info("Connected to database");
    return productRepository.findAll();
  }

  @Cacheable(unless = "#result?.score < 50") // ถ้า score < 50 จะไม่ cache
  public Optional<Product> find(Long id) {
    log.info("Connected to database");
    return productRepository.findById(id);
  }

  @CachePut(key = "#product.id") // update ค่าใน cache
  public Optional<Product> update(Product product) {
    return Optional.ofNullable(productRepository.save(product));
  }

  public Optional<Product> save(Product product) {
    return Optional.ofNullable(productRepository.save(product));
  }

  @CacheEvict
  public void delete(Long id) {
    productRepository.deleteById(id);
  }
}
