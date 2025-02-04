package com.chat_search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chat_search.dto.ChatMessageSearchResponseDTO;
import com.chat_search.entity.ChatMessageIndex;
import com.chat_search.service.ChatSearchService;

@RestController
@RequestMapping("/chat-search")
public class ChatSearchController {

    private final ChatSearchService chatSearchService;

    public ChatSearchController(ChatSearchService chatSearchService) {
        this.chatSearchService = chatSearchService;
    }

    @GetMapping("/conversation/{conversationId}")
    public List<ChatMessageIndex> getMessagesByConversationId(@PathVariable Long conversationId) {
        return chatSearchService.searchByConversationId(conversationId);
    }

    @GetMapping("/keyword")
    public List<ChatMessageSearchResponseDTO> searchMessagesByKeyword(@RequestParam String keyword) {
        return chatSearchService.searchByKeyword(keyword).stream()
                .map(msg -> new ChatMessageSearchResponseDTO(
                        msg.getConversationId(),
                        msg.getSenderId(),
                        msg.getReceiverId(),
                        msg.getMessage(),
                        msg.getSentAt()
                ))
                .toList();
    }

}
