package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class RemoveUserRequest extends ServerRequest{
    @JsonProperty
    private String userName;
    @JsonProperty
    private UUID chatId;

    public RemoveUserRequest(@JsonProperty("message") String message, @JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        this.userName = tokens[0];
        this.chatId = UUID.fromString(tokens[1]);
    }
    public String getUserName() {
        return userName;
    }

    public UUID getChatId() {
        return chatId;
    }
}
