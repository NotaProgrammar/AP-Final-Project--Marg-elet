package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class FindMultiChatForLink extends ServerRequest{
    @JsonProperty
    private UUID mucId;

    public FindMultiChatForLink(@JsonProperty("message") String message, @JsonProperty("sender")PrivateUser sender) {
        super(message, sender);
        mucId = UUID.fromString(message);
    }
    public UUID getMucId() {
        return mucId;
    }
}
