package com.github.nut077.springninja.service;

import com.github.nut077.springninja.dto.ProductDto;
import com.github.nut077.springninja.dto.mapper.ProductMapper;
import com.github.nut077.springninja.entity.Product;
import com.github.nut077.springninja.exception.NotFoundException;
import com.github.nut077.springninja.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.nut077.springninja.config.CaffeineCacheConfig.CacheName.PRODUCT;

@Log4j2
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = PRODUCT) // cache name default
public class ProductService {

  private final ProductRepository productRepository;
  private final RetryTemplate retryTemplate;
  private final ProductMapper mapper;

  @PostConstruct
  private void init() {
    log.info(this.getClass().getSimpleName() + ": Init");
  }

  //@Cacheable(cacheNames = PRODUCTS) // cache name is PRODUCTS
  public List<ProductDto> findAll(Product.Status status) {
    log.info("Connected to database");
    return mapper.map(StringUtils.isEmpty(status) ? productRepository.findAll() : productRepository.findAllByStatus(status));
  }

  @Cacheable(unless = "#result?.score < 50") // ถ้า score < 50 จะไม่ cache
  public ProductDto find(Long id) {
    log.info("Connected to database");
    return mapper.map(productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found id -->> " + id)));
  }

  public ProductDto save(ProductDto dto) {
    log.info(() -> "Save");
    return mapper.map(productRepository.save(mapper.map(dto)));
  }

  @CachePut(key = "#product.id") // update ค่าใน cache
  public ProductDto replace(Long id, ProductDto dto) {
    log.info(() -> "replace");
    find(id);
    return mapper.map(productRepository.save(mapper.map(dto)));
  }

  @CacheEvict(key = "#id")
  public void delete(Long id) {
    log.info(() -> "delete id " + id);
    productRepository.deleteById(id);
  }

  @Transactional
  @CacheEvict(key = "#id")
  public int updateScore(Long id, double score) {
    log.info(() -> "update score id = " + id + " score = " + score);
    return productRepository.updateScore(id, score);
  }

  @Async // จะใช้ได้เฉพาะ method ที่เป็น public เท่านั้น
  public CompletableFuture<Product> find(String name) throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
    log.info("[{}] Find product name[{}]", Thread.currentThread().getName(), name);
    return CompletableFuture.completedFuture(productRepository.findByName(name).orElse(null));
  }

  @Async // ใช้เทสกรณี exception
  public void voidMethod() {
    throw new RuntimeException("Test exception");
  }

  @Retryable(
    include = {RuntimeException.class}, // จะ retry เมื่อเกิด exception อะไร ถ้าไม่ใส่ ค่า default เป็นค่าว่าง คือจะ retry ทุกๆ exception
    exclude = {ArithmeticException.class}, // จะไม่ retry เมื่อเกิด exception อะไร ถ้าไม่ใส่ ค่า default เป็นค่าว่าง คือจะ retry ทุกๆ exception
    maxAttempts = 3, // ค่า default เท่ากับ 3 คือ จะ retry อีก 2 ครั้ง เพราะครั้งแรกที่เรียก method นี้ maxAttempts จะนับเป็น 1 retry ครั้งแรกนับเป็น 2 ครั้งต่อมานับเป็น 3
    backoff = @Backoff(value = 1000, multiplier = 2) // retry ครั้งแรกเริ่ม 1 วิ ครั้งต่อมา คือ 1000 * 2 == 2 วิ ครั้งต่อมา 2000 * 2 == 4 วิ
  )
  public void retry(String num) {
    int i;
    try {
      log.info("test Retry");
      i = Integer.parseInt(num);
    } catch (RuntimeException e) {
      throw new RuntimeException("throw -->> " + e.getMessage());
    }
    System.out.println(i);
  }

  @Recover // ถ้า retry type เป็นอะไร recover ก็ต้องเป็น type นั้น ตัวอย่าง void กับ void เหมือนกัน
  public void recover(RuntimeException e, String num) { // argument ตัวแรก คือ exception ที่เราดักจับ
    log.error("Exception is {}", e.getMessage());
    log.info("Num is {}", num);
  }

  public String retryTemplate() {
    AtomicInteger i = new AtomicInteger(1);
    log.info("Process other business logic");
    // ...
    // ...
    return retryTemplate.execute(
      retry -> {
        log.info("execute :: " + i.getAndIncrement() + " in normal process");
        throw new RuntimeException("RuntimeException");
      },
      recover -> {
        log.info("execute :: " + i.getAndIncrement() + " in recover callback");
        return "complete from recover callback";
      }
    );
  }
}
