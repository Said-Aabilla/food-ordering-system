package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper;

    public RestaurantApprovalOutboxCleanerScheduler(RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper) {
        this.restaurantApprovalOutboxHelper = restaurantApprovalOutboxHelper;
    }

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderApprovalOutboxMessage>> approvalOutboxMessages =
                restaurantApprovalOutboxHelper.getOrderApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.COMPENSATED,
                        SagaStatus.FAILED);

        if (approvalOutboxMessages.isPresent()) {
            List<OrderApprovalOutboxMessage> outboxMessages = approvalOutboxMessages.get();
            log.info("Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}",
                    outboxMessages.size(),
                    outboxMessages.stream().map(OrderApprovalOutboxMessage::getPayload)
                            .collect(Collectors.joining("\n")));

            restaurantApprovalOutboxHelper.deleteByOutboxStatusAndSagaStatus(
                    OutboxStatus.COMPLETED,
                    SagaStatus.SUCCEEDED,
                    SagaStatus.COMPENSATED,
                    SagaStatus.FAILED);
            log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
        }
    }
}
