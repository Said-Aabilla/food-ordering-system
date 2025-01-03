package com.food.ordering.system.order.service.domain.dto.create;


import com.food.ordering.system.order.service.messaging.order.service.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class CreateOrderResponse {


    @NotNull
    public final UUID trackingId;

    @NotNull
    public final OrderStatus orderStatus;

    @NotNull
    public final BigDecimal price;

    @NotNull
    public String message;

}
