package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class ChatReadRequest extends ServerRequest {
    @JsonProperty
    private UUID messageId;
    @JsonProperty
    private UUID chatId;
    @JsonProperty
    private String chatType;


    public ChatReadRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        this.messageId = UUID.fromString(tokens[0]);
        this.chatId = UUID.fromString(tokens[1]);
        this.chatType = tokens[2];
    }

    public UUID getMessageId() {
        return messageId;
    }
    public UUID getChatId() {
        return chatId;
    }
    public String getChatType() {
        return chatType;
    }
}
