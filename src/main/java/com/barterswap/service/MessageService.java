package com.barterswap.service;

import com.barterswap.dto.MessageRequest;
import com.barterswap.dto.MessageResponse;
import com.barterswap.entity.Item;
import com.barterswap.entity.Message;
import com.barterswap.entity.User;
import com.barterswap.repository.ItemRepository;
import com.barterswap.repository.MessageRepository;
import com.barterswap.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public MessageResponse sendMessage(Integer senderId, MessageRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));
        
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .item(item)
                .content(request.getContent())
                .build();

        Message savedMessage = messageRepository.save(message);
        return convertToResponse(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessagesForUser(Integer userId) {
        return messageRepository.findByReceiverUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessagesForItem(Integer itemId) {
        return messageRepository.findByItemItemIdAndIsDeletedFalse(itemId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessageAsRead(Integer messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Integer messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
        message.setIsDeleted(true);
        messageRepository.save(message);
    }

    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getUsername())
                .receiverId(message.getReceiver().getUserId())
                .receiverName(message.getReceiver().getUsername())
                .itemId(message.getItem().getItemId())
                .itemTitle(message.getItem().getTitle())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();
    }
} 