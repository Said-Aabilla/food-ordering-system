package com.food.ordering.system.order.service.data.access.restaurant.mapper;

import com.food.ordering.system.order.service.data.access.restaurant.exception.RestaurantDataAccessMapperException;
import com.food.ordering.system.order.service.data.access.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.Money;
import com.food.ordering.system.order.service.domain.valueobject.ProductId;
import com.food.ordering.system.order.service.domain.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts()
                .stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {

        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() ->
                        new RestaurantDataAccessMapperException("Restaurant Entity Could Not Be Found !")
                );

        List<Product> restaurantProducts = restaurantEntities.stream()
                .map(entity -> new Product(new ProductId(entity.getProductId()),
                        entity.getProductName(),
                        new Money(entity.getProductPrice()))
                )
                .toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .active(restaurantEntity.getRestaurantActive())
                .products(restaurantProducts)
                .build();
    }
}
