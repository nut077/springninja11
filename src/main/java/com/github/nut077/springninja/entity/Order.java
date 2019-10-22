package com.github.nut077.springninja.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "orders")
@Getter
@Setter
@EqualsAndHashCode
//@IdClass(OrderId.class)  ใช้ @IdClass ต้องมี field เหมือนกับ ตัวที่จะให้ เป็น key ทั้งหมด
public class Order {

  /*@Id
  private Long id;
  @Id
  private Long productId;*/

  @EmbeddedId // ใช้กับ class ที่ใส่ annotation @Embeddable
  @AttributeOverrides(
    value = {
      @AttributeOverride(name = "id", column = @Column(name = "od_id")),  // ใช้ในการเปลี่ยนชื่อ column
      @AttributeOverride(name = "productId", column = @Column(name = "pd_id"))
    }
  )
  private OrderId orderId;

  private int quantity;
}
