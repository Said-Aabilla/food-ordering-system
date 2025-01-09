package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {


    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment,
                                                   CreditEntry creditEntry,
                                                   List<CreditHistory> creditHistories,
                                                   List<String> failureMessages) {

        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistories(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistories(creditEntry, creditHistories, failureMessages);


        if(failureMessages.isEmpty()){
            log.info("Payment is initiated for order with id: {}", payment.getOrderId().getValue());
            payment.updatePaymentStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of("GMT")));
        }else{
            log.info("Payment initialization failed for order with id: {}", payment.getOrderId().getValue());
            payment.updatePaymentStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of("GMT")), failureMessages);
        }

    }


    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment,
                                                 CreditEntry creditEntry,
                                                 List<CreditHistory> creditHistories,
                                                 List<String> failureMessages) {

        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistories(payment, creditHistories, TransactionType.CREDIT);

        if(failureMessages.isEmpty()){
            log.info("Payment is cancelled for order with id: {}", payment.getOrderId().getValue());
            payment.updatePaymentStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of("GMT")));
        }else{
            log.info("Payment cancellation failed for order with id: {}", payment.getOrderId().getValue());
            payment.updatePaymentStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of("GMT")), failureMessages);
        }
    }



    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {

        if (payment.getPrice().isAmountGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error("Customer with id: {} doesn't have enough credit for payment!",
                    payment.getCustomerId().getValue());
            failureMessages.add("Customer with id: " + payment.getCustomerId().getValue() +
                    " doesn't have enough credit for payment!");
        }

    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistories(Payment payment,
                                       List<CreditHistory> creditHistories,
                                       TransactionType transactionType) {

        creditHistories.add(CreditHistory.builder()
                .id(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .transactionType(transactionType)
                .amount(payment.getPrice())
                .build());
    }

    private void validateCreditHistories(CreditEntry creditEntry,
                                         List<CreditHistory> creditHistories,
                                         List<String> failureMessages) {

        Money totalDebitHistory = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);
        Money totalCreditHistory = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);

        if (totalDebitHistory.isAmountGreaterThan(totalCreditHistory)) {
            log.error("Customer with id: {} doesn't have enough credit according to credit history!",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add("Customer with id: " + creditEntry.getCustomerId().getValue() +
                    " doesn't have enough credit according to credit history!");
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            log.error("Credit history total is not equal to current credit entry for customer with id: {}",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add("Credit history total is not equal to current credit entry for customer with id" +
                    creditEntry.getCustomerId().getValue());
        }
    }

    private Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {

        return creditHistories.stream().filter(creditHistory -> creditHistory.getTransactionType() == transactionType)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }
    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }
}
