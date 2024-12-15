package com.food.ordering.system.order.service.domain.dto.track;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;


@AllArgsConstructor
@Getter
@Builder
public class TrackOrderQuery {

    @NotNull
    public final UUID orderTrackingId;
}
