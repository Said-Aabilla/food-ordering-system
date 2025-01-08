package com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;


import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
