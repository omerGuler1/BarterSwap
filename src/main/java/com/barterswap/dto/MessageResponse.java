package com.barterswap.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Integer messageId;
    private Integer senderId;
    private String senderName;
    private Integer receiverId;
    private String receiverName;
    private Integer itemId;
    private String itemTitle;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
} 