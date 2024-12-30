package com.food.ordering.system.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class OrderId  extends BaseId<UUID> {

    public OrderId(UUID value) {
        super(value);
    }
}
