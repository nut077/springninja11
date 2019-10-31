package com.github.nut077.springninja.repository;

import com.github.nut077.springninja.entity.Order;
import com.github.nut077.springninja.entity.OrderId;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CommonRepository<Order, OrderId> {
}
