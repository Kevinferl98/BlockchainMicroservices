package com.project.producer.dto;

import lombok.Data;

@Data
public class MessageEvent {
    private String category;
    private String description;
    private String timestamp;
    private int priority;
}
