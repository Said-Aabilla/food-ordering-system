package com.food.ordering.system.order.service.messaging.order.service.domain.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;


@AllArgsConstructor
@Getter
@Builder
public class CreateOrderItem {

    @NonNull
    public final UUID productId;

    @NonNull
    public final Integer quantity;

    @NonNull
    public final BigDecimal price;

    @NonNull
    public final BigDecimal subTotal;

}
