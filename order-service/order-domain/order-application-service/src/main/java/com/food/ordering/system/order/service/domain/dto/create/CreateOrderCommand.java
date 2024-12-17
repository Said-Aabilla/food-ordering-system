package com.food.ordering.system.order.service.domain.dto.create;


import com.food.ordering.system.domain.entity.OrderItem;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class CreateOrderCommandDto {


    @NotNull
    public final UUID customerId;

    @NotNull
    public final UUID restaurantId;

    @NotNull
    public final BigDecimal price;

    @NotNull
    public final List<OrderItem> orderItems;

    @NotNull
    public final OrderAddress orderAddress;

}
