package com.food.ordering.system.order.service.messaging.order.service.domain;


import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.messaging.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.messaging.order.service.domain.valueobject.TrackingId;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.track.TrackingOrderResponse;
import com.food.ordering.system.order.service.messaging.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.messaging.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Component
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    public OrderTrackCommandHandler(OrderDataMapper orderDataMapper, OrderRepository orderRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public TrackingOrderResponse trackOrder(TrackOrderQuery trackOrderQuery){

        Order order = findOrder(trackOrderQuery);
        log.info("Order found and tracked successfully with tracking id {}", order.getTrackingId().getValue());
        return  orderDataMapper.orderToTrackingOrderResponse(order);

    }


    private Order findOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> orderOptional = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
        if(orderOptional.isEmpty()){
            log.warn("Order could not be found with tracking id: {}", trackOrderQuery.getOrderTrackingId());
           throw new OrderNotFoundException("Could not find order with tracking id: " + trackOrderQuery.getOrderTrackingId());
        }
        return orderOptional.get();
    }

}
