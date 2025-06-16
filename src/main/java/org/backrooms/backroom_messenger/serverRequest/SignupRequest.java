package org.backrooms.backroom_messenger.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignupRequest extends ServerRequest{
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;
    @JsonProperty
    byte[] salt;

    public SignupRequest(@JsonProperty("message") String message) {
        super(message);
        username = message.split("--")[0];
        password = message.split("--")[1];
        salt = message.split("--")[2].getBytes();
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public byte[] getSalt() {
        return salt;
    }

}
