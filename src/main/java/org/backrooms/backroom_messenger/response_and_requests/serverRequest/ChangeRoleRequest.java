package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class ChangeRoleRequest extends ServerRequest{
    @JsonProperty
    private String userName;
    @JsonProperty
    private String role;
    @JsonProperty
    private UUID id;

    public ChangeRoleRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        id = UUID.fromString(tokens[0]);
        userName = tokens[1];
        role = tokens[2];
    }

    public String getUserName() {
        return userName;
    }
    public String getRole() {
        return role;
    }
    public UUID getId() {
        return id;
    }
}
