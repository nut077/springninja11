package com.github.nut077.springninja.repository;

import com.github.nut077.springninja.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(value = "update com.github.nut077.springninja.entity.Product p set p.name = :name where p.code = :code")
  void jpqlUpdate(@Param("name") String name, @Param("code") String code);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(value = "update products set name = :name where code = :code", nativeQuery = true)
  void sqlUpdate(String name, String code);
}
