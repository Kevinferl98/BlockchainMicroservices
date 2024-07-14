package com.project.consumer.processor;

import com.project.consumer.dto.MessageEvent;
import com.project.consumer.service.EthereumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

import static com.project.consumer.utils.HashGenerator.generateHash;

@Slf4j
@Service
public class MessageProcessor {

    private static final int PRIORITY_ONE = 1;
    private static final int PRIORITY_TWO = 2;
    private static final int PRIORITY_THREE = 3;
    private static final String CATEGORY_URGENT = "Urgent";

    @Autowired
    private EthereumService ethereumService;

    private Queue<MessageEvent> priorityQueue = new LinkedList<>();
    private int priorityOneCount = 0;

    public void processMessage(MessageEvent message) {
        switch (message.getPriority()) {
            case PRIORITY_ONE:
                process(message);
                priorityOneCount++;
                if(priorityOneCount % 3 == 0 && !priorityQueue.isEmpty()) {
                    log.info("Processing a message from the priority queue");
                    process(priorityQueue.poll());
                }
                break;
            case PRIORITY_TWO:
                if(CATEGORY_URGENT.equals(message.getCategory())) {
                    process(message);
                } else {
                    priorityQueue.add(message);
                    log.info("Message added to the priority queue");
                }
                break;
            case PRIORITY_THREE:
                if(CATEGORY_URGENT.equals(message.getCategory())) {
                    process(message);
                } else {
                    log.info("Message discarded");
                }
                break;
        }
    }

    public void process(MessageEvent message) {
        String hash = generateHash(message.getTimestamp(), message.getDescription());
        ethereumService.sendTransaction(hash);
    }
}
