package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignupRequest extends ServerRequest{
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;
    @JsonProperty
    byte[] salt;

    public SignupRequest(@JsonProperty("message") String message) {
        super(message,"not logged in");
        this.username = message.split("--")[0];
        this.password = message.split("--")[1];
        this.salt = message.split("--")[2].getBytes();
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
