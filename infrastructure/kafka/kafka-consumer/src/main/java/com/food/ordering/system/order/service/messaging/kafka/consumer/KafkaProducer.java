package com.food.ordering.system.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

public interface KafkaProducer <T extends SpecificRecordBase>{
    void receive(List<T> messages, List<Long> keys, List<Integer> positions, List<Long> offsets);
}
