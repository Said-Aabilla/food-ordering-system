package com.food.ordering.system.order.service.data.access.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntityId implements Serializable {
    private Long id;
    private OrderEntity orderEntity;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntityId that = (OrderItemEntityId) o;
        return Objects.equals(id, that.id) && Objects.equals(orderEntity, that.orderEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderEntity);
    }
}
