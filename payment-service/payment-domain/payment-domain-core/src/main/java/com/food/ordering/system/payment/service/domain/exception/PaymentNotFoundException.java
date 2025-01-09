package com.food.ordering.system.payment.service.domain.exception;

import com.food.ordering.system.domain.exception.DomainException;

public class PaymentNotFoundException extends DomainException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
