package com.food.ordering.system.payment.service.domain;


import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;
    private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;


    public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                                PaymentDataMapper paymentDataMapper,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentCompletedMessagePublisher paymentCompletedMessagePublisher,
                                PaymentCancelledMessagePublisher paymentCancelledMessagePublisher,
                                PaymentFailedMessagePublisher paymentFailedMessagePublisher) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentCompletedMessagePublisher = paymentCompletedMessagePublisher;
        this.paymentCancelledMessagePublisher = paymentCancelledMessagePublisher;
        this.paymentFailedMessagePublisher = paymentFailedMessagePublisher;
    }


    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {

        log.info("Received payment request for order with id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        CreditEntry creditEntry = getCustomerCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistory = getCustomerCreditHistory(payment.getCustomerId());

        List<String> failureMessages = new ArrayList<>();

        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment,
                creditEntry,
                creditHistory,
                failureMessages,
                paymentCompletedMessagePublisher,
                paymentFailedMessagePublisher);

        persistDbObjects(payment, creditEntry, creditHistory, failureMessages);

        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {

        Optional<Payment> paymentOptional = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

        if (paymentOptional.isEmpty()) {
            log.warn("Could not find Payment for Order id: {}", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Could not find Payment for Order id: " + paymentRequest.getOrderId());
        }

        Payment payment = paymentOptional.get();
        CreditEntry creditEntry = getCustomerCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistory = getCustomerCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment,
                creditEntry,
                creditHistory,
                failureMessages,
                paymentCancelledMessagePublisher,
                paymentFailedMessagePublisher);

        persistDbObjects(payment, creditEntry, creditHistory, failureMessages);

        return paymentEvent;
    }


    private CreditEntry getCustomerCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> entryOptional = creditEntryRepository.findByCustomerId(customerId);
        if (entryOptional.isEmpty()) {
            log.warn("Could not find Credit Entry for customer id: {}", customerId);
            throw new PaymentApplicationServiceException("Could not find Credit Entry for customer id: " + customerId);
        }
        return entryOptional.get();
    }

    private List<CreditHistory> getCustomerCreditHistory(CustomerId customerId) {

        Optional<List<CreditHistory>> creditHistoryOptional = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistoryOptional.isEmpty()) {
            log.warn("Could not find Credit History for customer id: {}", customerId);
            throw new PaymentApplicationServiceException("Could not find Credit History for customer id: " + customerId);
        }
        return creditHistoryOptional.get();
    }

    private void persistDbObjects(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistory, List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistory.get(creditHistory.size() - 1));
        }
    }

}
