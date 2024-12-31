package com.food.ordering.system.order.service.application.exception;

import com.food.ordering.system.application.common.handler.ErrorDto;
import com.food.ordering.system.application.common.handler.GlobalExceptionHandler;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {OrderDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(OrderDomainException orderDomainException) {
        log.error(orderDomainException.getMessage(), orderDomainException);
        return ErrorDto.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(orderDomainException.getMessage())
                .build();
    }


    @ResponseBody
    @ExceptionHandler(value = {OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(OrderNotFoundException orderNotFoundException) {
        log.error(orderNotFoundException.getMessage(), orderNotFoundException);
        return ErrorDto.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(orderNotFoundException.getMessage())
                .build();
    }
}
