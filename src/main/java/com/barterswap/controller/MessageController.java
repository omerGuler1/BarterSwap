package com.barterswap.controller;

import com.barterswap.dto.MessageRequest;
import com.barterswap.dto.MessageResponse;
import com.barterswap.entity.User;
import com.barterswap.repository.UserRepository;
import com.barterswap.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MessageRequest request) {
        // Fetch user by username
        User sender = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        return ResponseEntity.ok(messageService.sendMessage(sender.getUserId(), request));
    }

    @GetMapping("/user")
    public ResponseEntity<List<MessageResponse>> getUserMessages(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(messageService.getMessagesForUser(user.getUserId()));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<MessageResponse>> getItemMessages(@PathVariable Integer itemId) {
        return ResponseEntity.ok(messageService.getMessagesForItem(itemId));
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Integer messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }
} 