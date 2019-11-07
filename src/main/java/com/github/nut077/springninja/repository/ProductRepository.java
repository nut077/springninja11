package com.github.nut077.springninja.repository;

import com.github.nut077.springninja.dto.Pojo;
import com.github.nut077.springninja.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

  // query annotation
  @Query("select p from products p where p.name = ?1 and p.status = ?2")
  // Indexing parameters
  List<Product> selectAllByNameAndStatus(String name, Product.Status status);

  @Query("select p from products p where p.status = :status and p.name = :name")
    // Named parameters
  List<Product> selectAllByStatusAndName(@Param("status") Product.Status status, @Param("name") String name);

  @Query("select p from products p where name like %:name")
  List<Product> selectAllByNameEndsWith(@Param("name") String name);

  @Query(
    nativeQuery = true,
    value = "select p.* from products p where p.status = :status and parsedatetime(p.created_date,'yyyy-MM-dd') = :createdDate"
  )
  List<Product> selectAllByStatusAndDate(@Param("status") String status, @Param("createdDate") String createdDate);

  @Modifying // ถ้าจะอัพเดตต้องใส่ annotation นี้ด้วย
  @Transactional
  @Query("update products p set p.status = :status where p.code = :code")
  void updateStatusById(@Param("status") Product.Status status, @Param("code") String code);

  @Modifying
  @Transactional
  @Query(
    nativeQuery = true,
    value = "delete from products p where p.status = :status"
  )
  void removeAllByStatus(@Param("status") String status);

  // used for Async
  Optional<Product> findByName(String name);


  // updateScore
  @Modifying(clearAutomatically = true)
  @Query(value = "update products set score = :score where id = :id", nativeQuery = true)
  int updateScore(@Param("id") Long id, @Param("score") double score);

}
