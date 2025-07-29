package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserLogResponse extends ServerResponse {
    @JsonProperty
    private String username;
    @JsonProperty
    private Date lastSeen;
    @JsonProperty
    private boolean online;

    public UserLogResponse(@JsonProperty("message") String message) {
        super(message);
        String[] tokens = message.split("##");
        this.username = tokens[0];
        this.lastSeen = new Date(Long.parseLong(tokens[1]));
        this.online = tokens[2].equals("online");
    }

    public String getUsername() {
        return username;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public boolean isOnline() {
        return online;
    }
}
