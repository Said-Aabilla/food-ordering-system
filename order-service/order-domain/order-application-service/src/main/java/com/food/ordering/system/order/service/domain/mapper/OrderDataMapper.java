package com.food.ordering.system.order.service.messaging.order.service.domain.mapper;


import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.messaging.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.messaging.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.create.CreateOrderAddress;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.create.CreateOrderItem;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.messaging.order.service.domain.dto.track.TrackingOrderResponse;
import com.food.ordering.system.order.service.domain.valueobject.*;
import com.food.ordering.system.order.service.messaging.order.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getCreateOrderItems().stream().map(
                        createOrderItem -> new Product(new ProductId(createOrderItem.getProductId()))
                ).collect(Collectors.toList()))
                .build();
    }


    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(createOrderAddressToStreetAddress(createOrderCommand.getCreateOrderAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(createOrderItemsToOrderItems(createOrderCommand.getCreateOrderItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order orderResult, String message) {
        return  CreateOrderResponse.builder()
                .trackingId(orderResult.getTrackingId().getValue())
                .orderStatus(orderResult.getOrderStatus())
                .message(message)
                .build();
    }


    public TrackingOrderResponse orderToTrackingOrderResponse(Order order){
        return TrackingOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> createOrderItemsToOrderItems(List<CreateOrderItem> createOrderItems) {
        return createOrderItems.stream()
                .map(createOrderItem
                        -> OrderItem.builder()
                        .price(new Money(createOrderItem.getPrice()))
                        .product(new Product(new ProductId(createOrderItem.getProductId())))
                        .quantity(createOrderItem.getQuantity())
                        .subTotal(new Money(createOrderItem.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }

    private StreetAddress createOrderAddressToStreetAddress(CreateOrderAddress createOrderAddress) {
        return new StreetAddress(
                UUID.randomUUID(),
                createOrderAddress.getStreet(),
                createOrderAddress.getPostalCode(),
                createOrderAddress.getCity()
        );
    }
}
