package com.food.ordering.system.order.service.domain.dto.track;

import com.food.ordering.system.order.service.messaging.order.service.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class TrackingOrderResponse {

    @NotNull
    public final UUID orderTrackingId;

    @NotNull
    public final OrderStatus  orderStatus;

    private final List<String> failureMessages;
}
