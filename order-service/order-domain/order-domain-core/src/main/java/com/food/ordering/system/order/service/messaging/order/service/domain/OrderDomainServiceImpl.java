package com.food.ordering.system.order.service.messaging.order.service.domain;

import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.messaging.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.messaging.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.messaging.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.messaging.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
public class OrderDomainServiceImpl implements  OrderDomainService{
    private static final String TIMEZONE = "GMT";
    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(TIMEZONE)));
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id: {} is paid", order.getId().toString());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(TIMEZONE)));    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} is approved", order.getId().toString());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for order with id: {} ", order.getId().toString());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(TIMEZONE)));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled", order.getId().toString());
    }

    private void validateRestaurant(Restaurant restaurant) {
        if(!restaurant.isActive()){
            throw new OrderDomainException("Restaurant : " + restaurant.getId().getValue()+ " is not active for orders!");
        }
    }


    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        // Create a map for quick lookup of restaurant products by product reference
        Map<Product, Product> restaurantProductMap = restaurant.getProducts().stream()
                .collect(Collectors.toMap(product -> product, product -> product));

        // Iterate through order items and update product information
        order.getItems().forEach(orderItem -> {
            Product currentProduct = orderItem.getProduct();
            Product restaurantProduct = restaurantProductMap.get(currentProduct);

            if (restaurantProduct != null) {
                currentProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(),
                        restaurantProduct.getPrice());
            }
        });
    }

}
