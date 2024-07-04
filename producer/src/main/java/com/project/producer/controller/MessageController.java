package com.project.producer.controller;

import com.project.producer.dto.MessageEvent;
import com.project.producer.producer.MQProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

    private MQProducer mqProducer;

    public MessageController(MQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody MessageEvent messageEvent) {
        mqProducer.sendMessage(messageEvent);
        return ResponseEntity.ok("Message sent");
    }
}
