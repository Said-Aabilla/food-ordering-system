package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.saga.order.OrderConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class RestaurantApprovalOutboxHelper {


    private final ApprovalOutboxRepository approvalOutboxRepository;

    public RestaurantApprovalOutboxHelper(ApprovalOutboxRepository approvalOutboxRepository) {
        this.approvalOutboxRepository = approvalOutboxRepository;
    }


    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getOrderApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndOutboxStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getOrderApprovalOutboxMessageBySagaIdAndOutboxStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndOutboxStatus(ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage message = approvalOutboxRepository.save(orderApprovalOutboxMessage);
        if (message == null) {
            log.error("Could not save OrderApprovalOutboxMessage with outbox id: {}", orderApprovalOutboxMessage.getId().toString());
            throw new OrderDomainException("Could not save OrderApprovalOutboxMessage with outbox id: " + orderApprovalOutboxMessage.getId());
        }
    }

    @Transactional
    public void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME,
                outboxStatus,
                sagaStatus);
    }
}
