package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserReadResponse extends ServerResponse{
    @JsonProperty
    private UUID chatId;
    @JsonProperty
    private UUID msgId;
    @JsonProperty
    private String chatType;

    public UserReadResponse(@JsonProperty("message") String message) {
        super(message);
        String[] tokens = message.split("##");
        chatId = UUID.fromString(tokens[0]);
        msgId = UUID.fromString(tokens[1]);
        chatType = tokens[2];
    }

    public UUID getChatId() {
        return chatId;
    }
    public UUID getMsgId() {
        return msgId;
    }
    public String getChatType() {
        return chatType;
    }
}
