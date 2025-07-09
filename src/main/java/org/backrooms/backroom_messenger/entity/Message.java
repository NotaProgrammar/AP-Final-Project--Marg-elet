package org.backrooms.backroom_messenger.entity;

import java.util.Date;
import java.util.UUID;

public class Message {
    private UUID id;
    private User sender;
    private Chat chat;
    private String message;
    private Date timeDate;
    public Message(UUID id, User sender, Chat chat, String message, Date timeDate) {
        this.id = id;
        this.sender = sender;
        this.chat = chat;
        this.message = message;
        this.timeDate = timeDate;
    }
}
