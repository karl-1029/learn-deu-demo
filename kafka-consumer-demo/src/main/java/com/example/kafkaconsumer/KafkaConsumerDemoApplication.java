package com.example.kafkaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerDemoApplication.class, args);
    }
}
