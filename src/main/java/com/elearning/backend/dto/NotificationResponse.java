package com.elearning.backend.dto;

import com.elearning.backend.model.Notification;
import lombok.Data;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private boolean read;
    private String type;
    private String createdAt;

    public static NotificationResponse fromEntity(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setTitle(n.getTitle());
        r.setMessage(n.getMessage());
        r.setRead(n.getIsRead() != null ? n.getIsRead() : false); // ← corrigé
        r.setType(n.getType());
        r.setCreatedAt(n.getCreatedAt() != null
                ? n.getCreatedAt().toString() : "");
        return r;
    }
}