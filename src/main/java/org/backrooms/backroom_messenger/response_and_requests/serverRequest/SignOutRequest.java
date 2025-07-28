package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

public class SignOutRequest extends ServerRequest {

    @JsonProperty
    private String username;


    public SignOutRequest(@JsonProperty("message") String message, @JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        username = message;
    }

    public String getUsername() {
        return username;
    }
}
