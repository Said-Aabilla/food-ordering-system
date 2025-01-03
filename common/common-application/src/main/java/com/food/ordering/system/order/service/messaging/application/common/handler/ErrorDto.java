package com.food.ordering.system.order.service.messaging.application.common.handler;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorDto {
    public final String code;
    public final String message;
}
