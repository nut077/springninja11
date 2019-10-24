package com.github.nut077.springninja.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity(name = "orders")
@Getter
@Setter
@EqualsAndHashCode
//@IdClass(OrderId.class)  ใช้ @IdClass ต้องมี field เหมือนกับ ตัวที่จะให้ เป็น key ทั้งหมด
@ExcludeSuperclassListeners // จะได้ field ที่ extends มา แต่จะไม่ได้ความสามารถตามมาด้วย อย่างเช่นที่เราใส่ไว้ใน class Common คือ @PrePersist and @PreUpdate
@DynamicInsert // ใช้ในกรณีที่ไม่มี field ใหย๋ๆ ใน database อย่างเช่น CLOB NCLOB BLOB จะเรียก insert and update ก็ต่อเมื่อมีการเรียกใช้ field นั้นขึ้นมา ไม่ได้เรียกขึ้นมาทั้งหมด แต่ต้องคำนวณใหม่ทุกครั้ง ไม่ได้ใช้ cache เหมือนกับค่า default ที่ไม่ได้ใช้ annotaion นี้
@DynamicUpdate // ใช้ในกรณีที่ไม่มี field ใหย๋ๆ ใน database อย่างเช่น CLOB NCLOB BLOB จะเรียก insert and update ก็ต่อเมื่อมีการเรียกใช้ field นั้นขึ้นมา ไม่ได้เรียกขึ้นมาทั้งหมด แต่ต้องคำนวณใหม่ทุกครั้ง ไม่ได้ใช้ cache เหมือนกับค่า default ที่ไม่ได้ใช้ annotaion นี้
public class Order extends Common {

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

  @Lob
  private String details;  // CLOB  เก็บ text file ใหญ่

  @Lob
  @Nationalized
  private String nationalDetails;  // NCLOB คล้ายกับ CLOB แต่เก็บได้หลายภาษา

  @Lob
  private byte[] photos;  // BLOB  เก็บ image video audio
}
