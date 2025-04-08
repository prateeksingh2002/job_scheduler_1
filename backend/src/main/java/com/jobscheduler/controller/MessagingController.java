package com.jobscheduler.controller;

import com.jobscheduler.kafka.KafkaMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/messages")
public class MessagingController {

    private final KafkaMessageService kafkaMessageService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MessagingController(KafkaMessageService kafkaMessageService) {
        this.kafkaMessageService = kafkaMessageService;
    }

    @PostMapping("/delay")
    public ResponseEntity<String> sendMessageWithDelay(@RequestBody Map<String, Object> payload) {
        String topic = (String) payload.get("topic");
        String message = (String) payload.get("message");
        Integer delay = (Integer) payload.get("delayMinutes");

        scheduler.schedule(() -> kafkaMessageService.sendMessage(topic, message), delay, TimeUnit.MINUTES);

        return ResponseEntity.ok("Message scheduled for delivery in " + delay + " minutes.");
    }
}