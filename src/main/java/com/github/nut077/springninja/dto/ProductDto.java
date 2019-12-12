package com.github.nut077.springninja.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ProductDto {

  private Long id;
  private String code;
  private String name;
  private String description;
  private double score;
  //private double price;
  private String status;
  private String aliasNames;
  private Integer version;
  private OffsetDateTime updatedDate;
}
