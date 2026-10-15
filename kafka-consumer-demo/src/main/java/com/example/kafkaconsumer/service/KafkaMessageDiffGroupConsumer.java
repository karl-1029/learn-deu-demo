package com.example.kafkaconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageDiffGroupConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageDiffGroupConsumer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "demo-topic", groupId = "demo-group-1")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) throws Exception {
//        log.info("KafkaMessageDiffGroupConsumer Received Kafka record: {}-{}-{}-{}", record.topic(),record.key(), record.partition(), record.offset());
        String value = record.value();
        log.info("KafkaMessageDiffGroupConsumer value : {}", value);
        // ✅业务成功，手动提交offset
        acknowledgment.acknowledge();
    }
}
