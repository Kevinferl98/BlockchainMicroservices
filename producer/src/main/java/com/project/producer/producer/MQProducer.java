package com.project.producer.producer;

import com.project.producer.dto.MessageEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import static com.project.producer.utils.TimestampUtils.getTimestamp;

@Service
public class MQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.name}")
    private String routingKey;

    private static final Logger logger = LoggerFactory.getLogger(MQProducer.class);

    private RabbitTemplate rabbitTemplate;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;

    public MQProducer(RabbitTemplate rabbitTemplate, TaskScheduler taskScheduler) {
        this.rabbitTemplate = rabbitTemplate;
        this.taskScheduler = taskScheduler;
    }

    public void sendMessage() {
        MessageEvent messageEvent = createMessageEvent();
        logger.info("Message sent: {}", messageEvent.toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, messageEvent);
    }

    @PostConstruct
    public void startSendingMessages() {
        scheduleNextMessage();
    }

    private void scheduleNextMessage() {
        Duration delay = Duration.ofSeconds(new Random().nextInt(11) + 5);
        Trigger trigger = new Trigger() {
            @Override
            public Instant nextExecution(TriggerContext triggerContext) {
                return Instant.now().plus(delay);
            }
        };
        scheduledTask = this.taskScheduler.schedule(this::sendMessageAndReschedule, trigger);
    }

    private void sendMessageAndReschedule() {
        sendMessage();
        scheduleNextMessage();
    }

    private MessageEvent createMessageEvent() {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setPriority(new Random().nextInt(3) + 1);
        messageEvent.setCategory("");
        messageEvent.setDescription("");
        messageEvent.setTimestamp(getTimestamp());
        return messageEvent;
    }
}
