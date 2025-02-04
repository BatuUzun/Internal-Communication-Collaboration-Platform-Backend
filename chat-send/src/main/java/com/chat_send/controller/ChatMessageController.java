package com.chat_send.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat_send.dto.ChatMessageRequestDTO;
import com.chat_send.dto.CreateConversationRequest;
import com.chat_send.kafka.ChatMessageProducer;
import com.chat_send.proxy.ConversationProxy;
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

	@Autowired
	private ConversationProxy conversationProxy;

	@PostMapping("/send")
	public String sendMessage(@Valid @RequestBody ChatMessageRequestDTO chatMessageRequest) {
		try {
			// Set sentAt before sending to Kafka

			if (chatMessageRequest.getConversationId() == null) {
				CreateConversationRequest createConversationRequest = new CreateConversationRequest(
						chatMessageRequest.getReceiverId(), chatMessageRequest.getSenderId());
				chatMessageRequest.setConversationId(conversationProxy.createConversation(createConversationRequest));
			}
			if(chatMessageRequest.getConversationId() != null) {
				chatMessageRequest.setSentAt(Instant.now());

				// Convert message to JSON
				String messageJson = objectMapper.writeValueAsString(chatMessageRequest);

				// Send message to Kafka topic
				chatMessageProducer.sendMessage(CHAT_TOPIC, messageJson);
				
				return "Message sent to Kafka!";

			}
			return "Failed to send message: ConversationId is null!";
			

		} catch (Exception e) {
			return "Failed to send message: " + e.getMessage();
		}
	}

}