package com.project.producer.producer;

import com.google.gson.Gson;
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

    private static final String[] CATEGORIES = {"Urgent", "Slightly urgent", "Not urgent"};
    private static final String[] DESCRIPTIONS = {"Description1", "Description2", "Description3"};

    private RabbitTemplate rabbitTemplate;
    private final TaskScheduler taskScheduler;

    public MQProducer(RabbitTemplate rabbitTemplate, TaskScheduler taskScheduler) {
        this.rabbitTemplate = rabbitTemplate;
        this.taskScheduler = taskScheduler;
    }

    public void sendMessage() {
        MessageEvent messageEvent = createMessageEvent();
        logger.info("Message sent: {}", new Gson().toJson(messageEvent));
        rabbitTemplate.convertAndSend(exchange, routingKey, messageEvent);
    }

    @PostConstruct
    public void startSendingMessages() {
        scheduleNextMessage();
    }

    private void scheduleNextMessage() {
        Duration delay = Duration.ofSeconds(new Random().nextInt(21) + 10);
        Trigger trigger = new Trigger() {
            @Override
            public Instant nextExecution(TriggerContext triggerContext) {
                return Instant.now().plus(delay);
            }
        };
        this.taskScheduler.schedule(this::sendMessageAndReschedule, trigger);
    }

    private void sendMessageAndReschedule() {
        sendMessage();
        scheduleNextMessage();
    }

    private MessageEvent createMessageEvent() {
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setPriority(new Random().nextInt(3) + 1);
        messageEvent.setCategory(getRandomElement(CATEGORIES));
        messageEvent.setDescription(getRandomElement(DESCRIPTIONS));
        messageEvent.setTimestamp(getTimestamp());
        return messageEvent;
    }

    private static String getRandomElement(String[] array) {
        int index = new Random().nextInt(array.length);
        return array[index];
    }
}
