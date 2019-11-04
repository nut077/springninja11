package com.github.nut077.springninja.repository.specification;

import com.github.nut077.springninja.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {

  private ProductSpec() {

  }

  public static Specification<Product> nameEquals(String name) {
    return (product, query, cb) -> cb.equal(product.get("name"), name);
  }

  public static Specification<Product> nameLike(String name) {
    return (product, query, cb) -> {
      query.orderBy(cb.desc(product.get("name")));
      return cb.like(cb.lower(product.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Product> statusEquals(Product.Status status) {
    return (product, query, cb) -> cb.equal(product.get("status"), status);
  }

  public static Specification<Product> statusEqualsAndDetailIsNotNull(Product.Status status) {
    return (product, query, cb) -> cb.and(cb.equal(product.get("status"), status), cb.isNotNull(product.get("detail")));
  }

}
