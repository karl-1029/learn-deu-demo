package com.example.kafkaconsumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageDiffGroupConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageDiffGroupConsumer.class);


    @KafkaListener(topics = "demo-topic", groupId = "demo-group-1")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment)  {
//        log.info("KafkaMessageConsumer Received Kafka record: {}-{}-{}-{}", record.topic(),record.key(), record.partition(), record.offset());
        String value = record.value();
        log.info("KafkaMessageConsumer value : {}", value);
        if (value.contains("fail")) {
            log.info("Simulating processing failure for message: {}", value);
            throw new RuntimeException("consumer handler failed....");
        }
        acknowledgment.acknowledge();
    }
}
