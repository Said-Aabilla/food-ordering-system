package com.food.ordering.system.order.service.messaging.publisher.kafka;


import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.kafka.consumer.KafkaProducer;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.order.service.messaging.order.service.domain.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {


    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    public CreateOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData,
                                            OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderKafkaMessageHelper orderKafkaMessageHelper,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
        this.kafkaProducer = kafkaProducer;
    }


    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received Order Created Event for Order Id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentRequestTopicName(), paymentRequestAvroModel)
            );

            log.info("PaymentRequestAvroModel sent to kafka for Order Id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message for Order Id {}, error: {}",
                    orderId,
                    e.getMessage()
            );
        }
    }


}
