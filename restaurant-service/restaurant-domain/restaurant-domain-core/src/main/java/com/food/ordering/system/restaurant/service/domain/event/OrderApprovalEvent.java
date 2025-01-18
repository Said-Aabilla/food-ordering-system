package com.food.ordering.system.restaurant.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {

    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final List<String> failureMassages;
    private final ZonedDateTime zonedDateTime;

    public OrderApprovalEvent(OrderApproval orderApproval,
                              RestaurantId restaurantId,
                              List<String> failureMassages,
                              ZonedDateTime zonedDateTime) {
        this.orderApproval = orderApproval;
        this.restaurantId = restaurantId;
        this.failureMassages = failureMassages;
        this.zonedDateTime = zonedDateTime;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public List<String> getFailureMassages() {
        return failureMassages;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }
}
