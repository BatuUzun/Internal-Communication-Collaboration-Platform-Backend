package com.chat_send.kafka;

import com.chat_send.dto.ChatMessageRequestDTO;
import com.chat_send.entity.ChatMessage;
import com.chat_send.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ChatMessageConsumer {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public ChatMessageConsumer(ChatMessageRepository chatMessageRepository, ObjectMapper objectMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "chat_messages", groupId = "chat-group")
    public void consumeMessage(String messageJson) {
        try {
            // Convert JSON to DTO
            ChatMessageRequestDTO chatMessageRequest = objectMapper.readValue(messageJson, ChatMessageRequestDTO.class);

            // Convert DTO to Entity
            ChatMessage chatMessage = new ChatMessage(
                    chatMessageRequest.getConversationId(),
                    chatMessageRequest.getSenderId(),
                    chatMessageRequest.getReceiverId(),
                    chatMessageRequest.getMessage(),
                    chatMessageRequest.getSentAt()
            );

            // Save to Cassandra
            chatMessageRepository.save(chatMessage);
            System.out.println("Message saved to Cassandra: " + chatMessageRequest.getMessage());

        } catch (Exception e) {
            System.err.println("Failed to process Kafka message: " + e.getMessage());
        }
    }
}
