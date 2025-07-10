package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.PrivateUser;

public class LoginRequest extends ServerRequest{
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;


    public LoginRequest(@JsonProperty("message") String message) {
        super(message,new PrivateUser("not logged in","not logged in"));
        username = message.split("--")[0];
        password = message.split("--")[1];
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
