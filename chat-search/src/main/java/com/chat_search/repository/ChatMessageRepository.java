package com.chat_search.repository;

import com.chat_search.entity.ChatMessageIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ChatMessageRepository extends ElasticsearchRepository<ChatMessageIndex, String> {
    List<ChatMessageIndex> findByConversationId(Long conversationId);
    List<ChatMessageIndex> findByMessageContaining(String keyword);
}
