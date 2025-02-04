package com.chat_search.kafka;

import com.chat_search.dto.ChatMessageDTO;
import com.chat_search.entity.ChatMessageIndex;
import com.chat_search.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageConsumer {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public ChatMessageConsumer(ChatMessageRepository chatMessageRepository, ObjectMapper objectMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "chat_messages", groupId = "chat-search-group")
    public void consumeMessage(String messageJson) {
        try {
            ChatMessageDTO chatMessageDTO = objectMapper.readValue(messageJson, ChatMessageDTO.class);

            System.out.println("Received message from Kafka: " + messageJson);

            ChatMessageIndex chatMessageIndex = new ChatMessageIndex(
                    chatMessageDTO.getConversationId(),
                    chatMessageDTO.getSenderId(),
                    chatMessageDTO.getReceiverId(),
                    chatMessageDTO.getMessage(),
                    chatMessageDTO.getSentAt()
            );

            chatMessageRepository.save(chatMessageIndex);
            System.out.println("Indexed message: " + chatMessageDTO.getMessage());

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }
}
