package com.example.kafkaconsumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaDlqConfig {

    /**
     *
     * 责把最终处理失败的消息发回 Kafka，默认发到原 topic 名加 .DLT 的 topic，例如 demo-topic.DLT。
     * FixedBackOff(1000L, 2L)：每次重试间隔 1000 毫秒，最多重试 2 次；也就是初次消费失败后，额外重试两次。
     * 消费失败 → 等 1 秒重试 → 再失败 → 等 1 秒重试
     *          → 仍失败 → 发到 demo-topic.DLT
     * @param kafkaTemplate
     * @return
     */
    @Bean
    CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }
}
