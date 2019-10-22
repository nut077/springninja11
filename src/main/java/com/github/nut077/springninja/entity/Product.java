package com.github.nut077.springninja.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Immutable // จะทำให้ entity นั้นๆ ไม่สามารถแก้ไขได้ แต่สามารถเพิ่มและลบได้ มักจะใช้กับพวก entity master table,transaction log, รายชื่อประเทศ, รายชื่อค่าเงิน ที่โอกาสในการอัพเดตน้อย
@Entity(name = "products")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@SequenceGenerator(name = "products_seq")
@Table(
  indexes = {
    @Index(name = "products_idx_code_unique", columnList = "code", unique = true)
  }
)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
  private Long id;
  private String code;
  private String name;
}
