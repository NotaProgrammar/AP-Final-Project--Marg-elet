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
    @JsonProperty
    private String chatType;
    @JsonProperty
    private boolean read;

    public Message(@JsonProperty("id") UUID id,
                   @JsonProperty("sender") String  sender,
                   @JsonProperty("chat") UUID chat,
                   @JsonProperty("message") String message,
                   @JsonProperty("timeDate") Date timeDate,
                   @JsonProperty("chatType") String chatType,
                   @JsonProperty("read") boolean read) {
        this.id = id;
        this.sender = sender;
        this.chat = chat;
        this.message = formatMessage(message);
        this.timeDate = timeDate;
        this.chatType = chatType;
        this.read = read;
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
    public Date getDate(){
        return timeDate;
    }

    public String getSender() {
        return sender;
    }


    public String toString(String username) {
        if(username.equals(sender)) {
            return message + " read : " + read;
        }else{
            return message;
        }
    }

    public String formatMessage(String input) {
        int maxLineLength = 30;

        StringBuilder result = new StringBuilder();
        String line = input.replaceAll("\n", " ");

            int index = 0;

            while (index < line.length()) {
                int endIndex = Math.min(index + maxLineLength, line.length());

                // Try to break at the last space before maxLineLength
                int lastSpace = line.lastIndexOf(' ', endIndex);
                if (lastSpace > index) {
                    endIndex = lastSpace;
                }

                result.append(line, index, endIndex).append("\n");

                // Move to the next segment, skipping leading spaces
                index = endIndex;
                while (index < line.length() && line.charAt(index) == ' ') {
                    index++;
                }
            }

        return result.toString();
    }

    public String getChatType() {
        return chatType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
