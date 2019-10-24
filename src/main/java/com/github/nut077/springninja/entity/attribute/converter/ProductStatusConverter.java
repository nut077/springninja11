package com.github.nut077.springninja.entity.attribute.converter;

import com.github.nut077.springninja.entity.Product;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter(autoApply = true)
public class ProductStatusConverter implements AttributeConverter<Product.Status, String> {

  // convert จาก enum ให้เปลี่ยนเป็นค่าที่จะ save ลงใน database
  @Override
  public String convertToDatabaseColumn(Product.Status status) {
    return Objects.isNull(status) ? null : status.getCode();
  }

  // ของจาก database ให้ convert เป้น enum อะไรที่ต้องการ
  @Override
  public Product.Status convertToEntityAttribute(String code) {
    return Objects.isNull(code) ? null : Product.Status.codeToStatus(code);
  }
}
