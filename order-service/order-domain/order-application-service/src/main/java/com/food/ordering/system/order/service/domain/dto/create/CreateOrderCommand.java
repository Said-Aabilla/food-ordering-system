package com.food.ordering.system.order.service.domain.dto.create;


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
public class CreateOrderCommand {


    @NotNull
    public final UUID customerId;

    @NotNull
    public final UUID restaurantId;

    @NotNull
    public final BigDecimal price;

    @NotNull
    public final List<CreateOrderItem> createOrderItems;

    @NotNull
    public final CreateOrderAddress createOrderAddress;

}
