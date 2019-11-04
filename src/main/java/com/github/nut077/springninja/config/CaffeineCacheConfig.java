package com.github.nut077.springninja.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@Log4j2
public class CaffeineCacheConfig {

  @Bean
  public SimpleCacheManager buildSimpleCacheManager() {
    CaffeineCache productCache = buildCaffeineCache(CacheName.PRODUCT, 100);
    CaffeineCache productsCache = buildCaffeineCache(CacheName.PRODUCTS, 10);
    SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
    simpleCacheManager.setCaches(Arrays.asList(productCache, productsCache));
    simpleCacheManager.initializeCaches();
    return simpleCacheManager;
  }

  private CaffeineCache buildCaffeineCache(String name, long maxSize) {
    log.info(() -> "Build CaffeineCache[" + name + "], maximumSize[" + maxSize + "]");
    return new CaffeineCache(
      name,
      Caffeine.newBuilder()
        .softValues() // ถ้า garbage collection พบว่า memory ใกล้เต็ม จะทำการลบ value ที่อยู่ใน cache นั้นทิ้ง
        .maximumSize(maxSize) // ค่าสูงสุดที่จะเก็บ cache สมมติว่าให้เก็บแค่ 10 ถ้า ตัวที่ 11 เข้ามา ตัวที่ 1 จะถูกลบออกแล้วแทนที่ด้วยตัวที่ 11 ที่เข้ามาใหม่
        .expireAfterAccess(Duration.ofHours(1)) // กำหนดว่า record นั้นๆ ใน cache ถูก expire หลังจากถูก access เมื่อไร
        .expireAfterWrite(Duration.ofHours(24)) // คล้ายๆกับตัวบน ใช้คู่กับตัวบน หลังจากที่เขียนค่าเข้า cache จะถูก expire เมื่อไร set ไว้ 1 วัน หมายความว่า ต่อให้มีคน access เข้ามาเท่าไรก็ตาม แต่ถ้าครบ 1 วันเมื่อไรก็จะถูก expire ทิ้ง เพื่อโหลดค่ามาใหม่จาก database
        .build()
    );
  }

  @Caching(
    evict = {
      @CacheEvict(cacheNames = CacheName.PRODUCT, allEntries = true),
      @CacheEvict(cacheNames = CacheName.PRODUCTS, allEntries = true)
    }
  )
  public void evictAll() {
    log.info(() -> "Cache evict all");
  }

  public static class CacheName {
    public static final String PRODUCT = "PRODUCT";
    public static final String PRODUCTS = "PRODUCTS";

    private CacheName() {
    }
  }
}