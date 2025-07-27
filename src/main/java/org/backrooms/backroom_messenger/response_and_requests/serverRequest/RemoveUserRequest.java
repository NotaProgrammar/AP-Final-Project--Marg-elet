package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class RemoveUserRequest extends ServerRequest{
    @JsonProperty
    private String userName;
    @JsonProperty
    private String chatType;
    @JsonProperty
    private UUID chatId;

    public RemoveUserRequest(@JsonProperty("message") String message, @JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        this.userName = tokens[0];
        this.chatType = tokens[1];
        this.chatId = UUID.fromString(tokens[2]);
    }
    public String getUserName() {
        return userName;
    }
    public String getChatType() {
        return chatType;
    }
    public UUID getChatId() {
        return chatId;
    }
}
