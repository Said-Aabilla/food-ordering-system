package com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval;


import com.food.ordering.system.order.service.messaging.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.messaging.order.service.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
