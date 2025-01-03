package com.food.ordering.system.order.service.messaging.order.service.domain.entity;

import com.food.ordering.system.order.service.messaging.order.service.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    public Customer() {
    }
    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
