package com.chat_send.repository;

import com.chat_send.entity.ChatMessage;
import com.chat_send.entity.ChatMessageId;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ChatMessageRepository extends CassandraRepository<ChatMessage, ChatMessageId> {
    // No custom queries; use default CassandraRepository methods
}
