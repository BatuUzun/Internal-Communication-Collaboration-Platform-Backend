package com.chat_search.kafka;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.chat_search.dto.ChatMessageDTO;
import com.chat_search.entity.ChatMessageIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageConsumer {

    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;
    private final List<BulkOperation> bulkOperations = new ArrayList<>();

    public ChatMessageConsumer(ElasticsearchClient elasticsearchClient, ObjectMapper objectMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "chat_messages", groupId = "chat-search-group")
    public void consumeMessage(String messageJson) {
        try {
            ChatMessageDTO chatMessageDTO = objectMapper.readValue(messageJson, ChatMessageDTO.class);

            ChatMessageIndex chatMessageIndex = new ChatMessageIndex(
                    chatMessageDTO.getConversationId(),
                    chatMessageDTO.getSenderId(),
                    chatMessageDTO.getReceiverId(),
                    chatMessageDTO.getMessage(),
                    chatMessageDTO.getSentAt()
            );

            bulkOperations.add(BulkOperation.of(b -> b.index(idx -> idx
                    .index("chat_messages")
                    .document(chatMessageIndex)
            )));

            if (bulkOperations.size() >= 50) {
                executeBulkInsert();
            }

        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
        }
    }

    private void executeBulkInsert() {
        try {
            BulkRequest bulkRequest = BulkRequest.of(b -> b.operations(bulkOperations));
            elasticsearchClient.bulk(bulkRequest);
            bulkOperations.clear();
            System.out.println("Bulk insert executed.");
        } catch (Exception e) {
            System.err.println("Bulk insert failed: " + e.getMessage());
        }
    }
}
