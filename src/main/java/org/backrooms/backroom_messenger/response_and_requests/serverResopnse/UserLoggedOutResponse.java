package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserLoggedOutResponse extends ServerResponse {
    @JsonProperty
    private String username;
    @JsonProperty
    private Date lastSeen;

    public UserLoggedOutResponse(@JsonProperty("message") String message) {
        super(message);
        String[] tokens = message.split("##");
        this.username = tokens[0];
        this.lastSeen = new Date(Long.parseLong(tokens[1]));
    }

    public String getUsername() {
        return username;
    }

    public Date getLastSeen() {
        return lastSeen;
    }
}
