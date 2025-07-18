package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

public class Message {
    @JsonProperty
    private UUID id;
    @JsonProperty
    private String sender;
    @JsonProperty
    private UUID chat;
    @JsonProperty
    private String message;
    @JsonProperty
    private Date timeDate;


    private ObjectMapper mapper = new ObjectMapper();

    public Message(@JsonProperty("id") UUID id,@JsonProperty("sender") String  sender,@JsonProperty("chat") UUID chat,@JsonProperty("message") String message,@JsonProperty("timeDate") Date timeDate) {
        this.id = id;
        this.sender = sender;
        this.chat = chat;
        this.message = message;
        this.timeDate = timeDate;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChat() {
        return chat;
    }

    public String getMessage() {
        return message;
    }

    @JsonIgnore
    public LocalDate getTimeDate() {
        LocalDate localDate = timeDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return message;
    }
}
