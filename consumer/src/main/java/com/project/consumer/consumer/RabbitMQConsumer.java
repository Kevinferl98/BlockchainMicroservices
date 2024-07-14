package com.project.consumer.consumer;

import com.project.consumer.dto.MessageEvent;
import com.project.consumer.processor.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    @Autowired
    private MessageProcessor messageProcessor;

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(MessageEvent message) {
        logger.info("Received message: {}", message.toString());
        messageProcessor.processMessage(message);
    }
}
