package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.service.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final KafkaMessageProducer kafkaMessageProducer;

    @PostMapping("/messages/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> payload) {
        String message = payload == null || payload.isEmpty() ? "hello kafka" : payload.getOrDefault("message", "hello kafka");
        kafkaMessageProducer.sendMessage(message);

        return Map.of(
                "success", true,
                "topic", "demo-topic",
                "message", message
        );
    }
}
