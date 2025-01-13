package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper) {
        this.paymentRequestHelper = paymentRequestHelper;
    }


    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvent(paymentEvent);
    }



    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    private void fireEvent( PaymentEvent paymentEvent) {
        log.info("Publishing Payment Event with Payment id: {} and Order id: {}", paymentEvent.getPayment().getId(),
                paymentEvent.getPayment().getOrderId());

        paymentEvent.fire();
    }

    /*
        This was the old approach before adding the fire method in DomainEvent Interface
        extend it in the 3 PaymentEvent-Type- classes and adding a property of type :
        DomainEventPublisher<PaymentEvent-Type-> to be used inside the fire() method that will
        invoke the publish() method of domain event, which means the payment event object itself
        will invoke it through fire().

            private void fireEvent(PaymentEvent paymentEvent) {
                log.info("Publishing Payment Event with Payment id: {} and Order id: {}", paymentEvent.getPayment().getId(),
                        paymentEvent.getPayment().getOrderId());
                if (paymentEvent instanceof PaymentCompletedEvent) {
                    paymentCompletedMessagePublisher.publish((PaymentCompletedEvent) paymentEvent);
                } else if (paymentEvent instanceof PaymentCancelledEvent) {
                    paymentCancelledMessagePublisher.publish((PaymentCancelledEvent) paymentEvent);
                } else if (paymentEvent instanceof PaymentFailedEvent) {
                    paymentFailedMessagePublisher.publish((PaymentFailedEvent) paymentEvent);
                }
               }
    */
}
