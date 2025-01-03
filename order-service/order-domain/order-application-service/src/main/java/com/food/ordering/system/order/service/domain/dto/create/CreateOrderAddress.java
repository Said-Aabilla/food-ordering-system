package com.food.ordering.system.order.service.messaging.order.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@AllArgsConstructor
@Getter
@Builder
public class CreateOrderAddress {

    @NotNull
    @Max(value = 10)
    public final String postalCode;

    @NotNull
    @Max(value = 50)
    public final String street;

    @NotNull
    @Max(value = 20)
    public final String city;
}
