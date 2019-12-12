package com.github.nut077.springninja.dto.mapper;

import com.github.nut077.springninja.dto.ProductDto;
import com.github.nut077.springninja.entity.Product;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.springframework.util.Assert.*;

@Mapper(
  componentModel = "spring",
  uses = SetMapper.class,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductMapper {

  @Mapping(source = "detail", target = "description")
  ProductDto map(Product entity);

  @BeforeMapping
  default void validate(ProductDto dto) {
    //isNull(dto.getId(), () -> "Id must be null");
    hasText(dto.getCode(), () -> "Code must has text");
    doesNotContain(dto.getCode(), "xxx", "Code mustn't contain xxx");
    hasText(dto.getName(), () -> "Name must has text");
    isTrue(dto.getScore() > 0, () -> "Score must be positive");
    //isTrue(dto.getPrice() > 0, () -> "Price must be positive");
    isTrue(
      Stream.of(Product.Status.values())
      .parallel()
      .anyMatch(status -> status.name().equalsIgnoreCase(dto.getStatus())),
      () -> "Status not found in an enum constant"
    );
    isNull(dto.getVersion(), () -> "Version must be null");
    isNull(dto.getUpdatedDate(), () -> "Updated date must be null");
  }

  @AfterMapping
  @Mapping(target = "description", ignore = true)
  default void afterMapping(Product entity, @MappingTarget ProductDto dto) {
    dto.setUpdatedDate(
      Objects.nonNull(dto.getUpdatedDate()) ? dto.getUpdatedDate() : entity.getCreatedDate()
    );
  }

  List<ProductDto> map(Collection<Product> entity);

  @Mapping(source = "description", target = "detail")
  Product map(ProductDto dto);

  List<Product> map(List<ProductDto> dto);
}
