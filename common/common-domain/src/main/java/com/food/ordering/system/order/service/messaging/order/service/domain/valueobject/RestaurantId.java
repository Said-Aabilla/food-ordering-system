package com.food.ordering.system.order.service.messaging.order.service.domain.valueobject;

import java.util.UUID;

public class RestaurantId extends BaseId<UUID> {

    public RestaurantId(UUID value) {
        super(value);
    }
}