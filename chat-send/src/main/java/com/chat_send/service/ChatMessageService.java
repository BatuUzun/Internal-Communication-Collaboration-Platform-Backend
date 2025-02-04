package com.chat_send.service;

import com.chat_send.entity.ChatMessage;
import com.chat_send.repository.ChatMessageRepository;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(Long conversationId, Long senderId, Long receiverId, String message) {
        // Generate a TIMEUUID for message_id
        UUID messageId = Uuids.timeBased();
        Instant sentAt = Instant.now();

        // Create a ChatMessage object
        ChatMessage chatMessage = new ChatMessage(conversationId, senderId, receiverId, message, sentAt);

        // Save the entity using the default save() method
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessagesByConversationId(Long conversationId) {
        // Use the findAll() method with filtering if needed
        return chatMessageRepository.findAll()
                .stream()
                .filter(msg -> msg.getId().getConversationId().equals(conversationId))
                .toList();
    }
}
