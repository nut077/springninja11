package com.github.nut077.springninja.repository;

import com.github.nut077.springninja.dto.Pojo;
import com.github.nut077.springninja.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CommonRepository<Product, Long> {

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(value = "update com.github.nut077.springninja.entity.Product p set p.name = :name where p.code = :code")
  void jpqlUpdate(@Param("name") String name, @Param("code") String code);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(value = "update products set name = :name where code = :code", nativeQuery = true)
  void sqlUpdate(String name, String code);

  // query method
  Optional<Collection<Product>> findAllByStatus(Product.Status status);
  Optional<Collection<Product>> findAllByStatusOrderByIdDesc(Product.Status status);
  Optional<Collection<Product>> findAllByNameContaining(String name);
  Optional<Collection<Product>> findAllByNameContainingIgnoreCase(String name);
  Optional<Collection<Product>> findAllByCodeContainingAndNameEndingWith(String code, String name);
  Optional<Collection<Product>> findAllByCodeOrCodeAndName(String whereCode, String orCode, String andName);

  // name native query
  Optional<List<Product>> fetchDetailNotNull();
  Optional<List<Product>> fetchDetailLengthGreaterThan2();
  @Query(nativeQuery = true)
  List<Pojo> customFetchProductToPojo();
}
