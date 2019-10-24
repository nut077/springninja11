package com.github.nut077.springninja.entity;

import lombok.*;

import javax.persistence.*;
import java.util.stream.Stream;

//@Immutable จะทำให้ entity นั้นๆ ไม่สามารถแก้ไขได้ แต่สามารถเพิ่มและลบได้ มักจะใช้กับพวก entity master table,transaction log, รายชื่อประเทศ, รายชื่อค่าเงิน ที่โอกาสในการอัพเดตน้อย
@Entity(name = "products")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@SequenceGenerator(name = "products_seq")
@Table(
  indexes = {
    @Index(name = "products_idx_code_unique", columnList = "code", unique = true),
    @Index(name = "products_idx_status", columnList = "status")
  }
)
public class Product extends Common {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_seq")
  private Long id;
  private String code;
  private String name;
  private String detail;

  //@Enumerated(value = EnumType.ORDINAL) default คือ ORDINAL จะเก็บข้อมูลเป็น 0,1,2....
  //@Enumerated(value = EnumType.STRING) เก็บข้อมูลเป็นคำๆ
  //@Enumerated(value = EnumType.STRING)

  @Column(length = 1)
  private Status status;

  @RequiredArgsConstructor
  public enum Status {
    APPROVED("A"),
    NOT_APPROVED("N"),
    PENDING("P");

    @Getter
    private final String code;

    public static Product.Status codeToStatus(String code) {
      return Stream.of(Product.Status.values())
        .parallel()
        .filter(status -> status.getCode().equalsIgnoreCase(code))
        .findAny()
        .orElseThrow(
          () -> new IllegalArgumentException("The code : " + code + " is illegal argument."));
    }
  }

}
