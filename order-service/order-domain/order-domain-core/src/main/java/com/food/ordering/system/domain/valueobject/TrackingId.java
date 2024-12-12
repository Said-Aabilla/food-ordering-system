package com.food.ordering.system.domain.valueobject;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    protected TrackingId(UUID value) {
        super(value);
    }
}
