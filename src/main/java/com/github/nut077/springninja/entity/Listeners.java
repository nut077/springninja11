package com.github.nut077.springninja.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.OffsetDateTime;

class Listeners<T extends Common> {

  @PrePersist
  private void prePersist(T e) {
    e.setCreatedDate(OffsetDateTime.now());
  }

  @PreUpdate
  private void preUpdate(T e) {
    e.setUpdatedDate(OffsetDateTime.now());
  }
}
