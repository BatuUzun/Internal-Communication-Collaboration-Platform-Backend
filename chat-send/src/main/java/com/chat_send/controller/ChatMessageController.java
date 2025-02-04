package com.chat_send.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat_send.dto.ChatMessageRequestDTO;
import com.chat_send.kafka.ChatMessageProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chat-send")
public class ChatMessageController {

	@Autowired
    private ChatMessageProducer chatMessageProducer;

    @Autowired
    private ObjectMapper objectMapper; // For JSON serialization

    private static final String CHAT_TOPIC = "chat_messages";

    @PostMapping("/send")
    public String sendMessage(@Valid @RequestBody ChatMessageRequestDTO chatMessageRequest) {
        try {
            // ✅ Set sentAt before sending to Kafka
            chatMessageRequest.setSentAt(Instant.now());

            // Convert message to JSON
            String messageJson = objectMapper.writeValueAsString(chatMessageRequest);

            // Send message to Kafka topic
            chatMessageProducer.sendMessage(CHAT_TOPIC, messageJson);

            return "Message sent to Kafka!";
        } catch (Exception e) {
            return "Failed to send message: " + e.getMessage();
        }
    }

}