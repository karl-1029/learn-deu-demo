package com.example.kafkaconsumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaDLQMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaDLQMessageConsumer.class);


    @KafkaListener(topics = "demo-topic.DLT", groupId = "dlt-demo-group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment)  {
        String value = record.value();
        log.info("KafkaDLQMessageConsumer value : {}", value);
        acknowledgment.acknowledge();
    }
}
