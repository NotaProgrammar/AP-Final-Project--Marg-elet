package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

public class ChangeUserPropertyRequest extends ServerRequest{
    @JsonProperty
    private String name;
    @JsonProperty
    private String password;

    public ChangeUserPropertyRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        this.name = tokens[0];
        this.password = tokens[1];
    }

    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
}
