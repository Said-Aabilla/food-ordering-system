package com.food.ordering.system.order.service.messaging.order.service.domain.event.publisher;

import com.food.ordering.system.order.service.messaging.order.service.domain.event.DomainEvent;

public interface DomainEventPublisher <T extends DomainEvent> {
    void publish(T domainEvent);
}
