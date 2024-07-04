package com.project.producer.producer;

import com.project.producer.dto.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.name}")
    private String routingKey;

    private static final Logger logger = LoggerFactory.getLogger(MQProducer.class);

    private RabbitTemplate rabbitTemplate;

    public MQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(MessageEvent messageEvent) {
        logger.info("Message sent: {}", messageEvent.toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, messageEvent);
    }
}
