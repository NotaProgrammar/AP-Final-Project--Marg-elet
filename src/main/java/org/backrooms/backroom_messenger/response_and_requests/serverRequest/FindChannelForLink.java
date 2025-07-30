package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class FindChannelForLink extends ServerRequest{
    @JsonProperty
    private UUID channelId;

    public FindChannelForLink(@JsonProperty("message") String message, @JsonProperty("sender")PrivateUser sender) {
        super(message, sender);
        channelId = UUID.fromString(message);
    }
    public UUID getChannelId() {
        return channelId;
    }
}
