package com.github.nut077.springninja.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class OrderId implements Serializable {

  private Long id;
  private Long productId;
}
