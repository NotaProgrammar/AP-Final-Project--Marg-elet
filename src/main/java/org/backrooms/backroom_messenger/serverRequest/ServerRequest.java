package org.backrooms.backroom_messenger.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.fxml.FXML;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.User;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = "loginRequest"),
        @JsonSubTypes.Type(value = SignupRequest.class, name = "signupRequest")
})

public abstract class ServerRequest{

    @JsonProperty
    private String message;

    public ServerRequest(String message) {
        this.message = message;
    }

}
