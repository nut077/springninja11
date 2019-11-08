package com.github.nut077.springninja.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OrderId implements Serializable {

  private Long id;
  private Long productId;
}
