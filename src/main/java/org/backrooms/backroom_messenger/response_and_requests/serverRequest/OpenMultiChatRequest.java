package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class OpenMultiChatRequest extends ServerRequest{
    @JsonProperty
    private UUID id;

    public OpenMultiChatRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        id = UUID.fromString(message);
    }

    public UUID getId() {
        return id;
    }
}
