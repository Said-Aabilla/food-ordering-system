package com.food.ordering.system.order.service.messaging.publisher.kafka;


import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Slf4j
@Component
public class OrderKafkaMessageHelper {

    public ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>>
    getKafkaCallback(String paymentResponseTopicName, PaymentRequestAvroModel paymentRequestAvroModel) {
        return new ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending PaymentRequestAvroModel " +
                                "message {} to topic {}",
                        paymentRequestAvroModel.toString(),
                        paymentResponseTopicName,
                        ex
                );
            }

            @Override
            public void onSuccess(SendResult<String, PaymentRequestAvroModel> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();

                log.error("Received successful response from Kafka for order is {}" +
                                "Topic: {} Partition: {} Offset: {} TimeStamp: {}",
                        paymentRequestAvroModel.getOrderId(),
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset(),
                        recordMetadata.timestamp()
                );
            }
        };
    }
}
