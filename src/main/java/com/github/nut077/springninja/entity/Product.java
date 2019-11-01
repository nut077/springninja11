package com.github.nut077.springninja.entity;

import com.github.nut077.springninja.dto.Pojo;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

//@Immutable จะทำให้ entity นั้นๆ ไม่สามารถแก้ไขได้ แต่สามารถเพิ่มและลบได้ มักจะใช้กับพวก entity master table,transaction log, รายชื่อประเทศ, รายชื่อค่าเงิน ที่โอกาสในการอัพเดตน้อย
@Entity(name = "products")
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "products_seq")
@Table(
  indexes = {
    @Index(name = "products_idx_code_unique", columnList = "code", unique = true),
    @Index(name = "products_idx_status", columnList = "status")
  }
)

@NamedQueries({
  @NamedQuery(
    name = "Product.fetchDetailNotNull",
    query = "select p from products p where p.detail is not null"),
  @NamedQuery(
    name = "Product.fetchDetailLengthGreaterThan2",
    query = "select p from products p where length(p.detail) > 2")
})

// @SqlResultSetMapping ใช้คู่กับ @NamedNativeQueries
@SqlResultSetMapping(
  name = "pojo",
  classes = {
    @ConstructorResult(
      targetClass = Pojo.class,
      columns = {
        @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "codeAndName", type = String.class),
        @ColumnResult(name = "detail", type = String.class)
      }
    )
  }
)
@NamedNativeQueries({
  @NamedNativeQuery(
    name = "Product.customFetchProductToPojo",
    query = "select p.id, p.code||' : '||p.name as codeAndName, p.detail from products p",
    resultSetMapping = "pojo")
})
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

  @ElementCollection(fetch = FetchType.EAGER) // มีไว้สำหรับ map field ที่เป็น collection พวก Set หรือว่า list ของตัวแปรที่เป็น basic type หรือ ตัวแรปที่เป็น class ที่ map ด้วย annotation @Embeddable
                            // ชื่อ table                                                     // field ที่ใช้ join
  @CollectionTable(name = "products_alias_names_custom", joinColumns = @JoinColumn(name = "products_id_custom")) // ถ้าต้องการเปลี่ยนชื่อตาราง
  private Set<String> aliasNames = new HashSet<>();

  @RequiredArgsConstructor
  public enum Status {
    APPROVED("A"),
    NOT_APPROVED("N"),
    PENDING("P");

    @Getter
    private final String code;

    public static Status codeToStatus(String code) {
      return Stream.of(Status.values())
        .parallel()
        .filter(status -> status.getCode().equalsIgnoreCase(code))
        .findAny()
        .orElseThrow(
          () -> new IllegalArgumentException("The code : " + code + " is illegal argument."));
    }
  }

}
