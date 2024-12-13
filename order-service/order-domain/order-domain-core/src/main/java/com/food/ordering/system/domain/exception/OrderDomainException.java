package com.food.ordering.system.domain.exception;

public class OrderDomainException  extends DomainException{



    public OrderDomainException(String message) {
        super(message);
    }

    public OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
