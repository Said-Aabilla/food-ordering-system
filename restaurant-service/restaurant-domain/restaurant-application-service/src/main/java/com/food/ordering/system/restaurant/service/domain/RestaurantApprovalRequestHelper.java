package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {


    public final RestaurantDomainService restaurantDomainService;
    public final RestaurantRepository restaurantRepository;
    public final OrderApprovalRepository orderApprovalRepository;
    public final RestaurantDataMapper restaurantDataMapper;
    public final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    public final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovalRepository orderApprovalRepository,
                                           RestaurantDataMapper restaurantDataMapper,
                                           OrderApprovedMessagePublisher orderApprovedMessagePublisher,
                                           OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.restaurantDataMapper = restaurantDataMapper;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }


    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing restaurant approval request for order id: {}", restaurantApprovalRequest.getOrderId());
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        List<String> failureMessages = new ArrayList<>();
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant,
                failureMessages,
                orderApprovedMessagePublisher,
                orderRejectedMessagePublisher);

        orderApprovalRepository.save(restaurant.getOrderApproval());
        return orderApprovalEvent;
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if(optionalRestaurant.isEmpty()){
            log.error("Restaurant with id "+restaurant.getId().getValue()+" not found!");
            throw new RestaurantNotFoundException("Restaurant with id "+restaurant.getId().getValue()+" not found!");
        }
        Restaurant restaurantEntity = optionalRestaurant.get();
        restaurant.setActive(restaurantEntity.isActive());

        return updateOrderDetail(restaurantEntity, restaurant, restaurantApprovalRequest);
    }

    private Restaurant updateOrderDetail(Restaurant restaurantEntity, Restaurant restaurant, RestaurantApprovalRequest restaurantApprovalRequest) {
        // Create a map for quick lookup of restaurant products by product reference
        Map<Product, Product> restaurantEntityProductMap = restaurantEntity.getOrderDetail().getProducts().stream()
                .collect(Collectors.toMap(product -> product, product -> product));

        // Iterate and  update product information
        restaurant.getOrderDetail().getProducts().forEach(p -> {
            Product product = restaurantEntityProductMap.get(p);

            if (product != null) {
                p.updateWithConfirmedNameAndPriceAndAvailability(product.getName(),
                        product.getPrice(),
                        product.isAvailable());
            }
        });

        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));
        return restaurant;
    }


}
