package com.food.ordering.system.order.service.messaging.kafka.producer.exception;

public class KafkaProducerException  extends RuntimeException{
    public KafkaProducerException(String message) {
        super(message);
    }
}
