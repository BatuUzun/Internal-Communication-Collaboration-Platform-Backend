package com.chat_search.service;

import com.chat_search.entity.ChatMessageIndex;
import com.chat_search.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatSearchService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatSearchService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessageIndex> searchByConversationId(Long conversationId) {
        return chatMessageRepository.findByConversationId(conversationId);
    }

    public List<ChatMessageIndex> searchByKeyword(String keyword) {
        return chatMessageRepository.findByMessageContaining(keyword);
    }
}
