package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = "loginRequest"),
        @JsonSubTypes.Type(value = SignupRequest.class, name = "signupRequest"),
        @JsonSubTypes.Type(value = SearchRequest.class, name = "searchRequest"),
        @JsonSubTypes.Type(value = NewChatRequest.class, name = "newChatRequest")
})

public abstract class ServerRequest {

    @JsonProperty
    private String message;
    @JsonProperty
    private String username;

    public ServerRequest(String message,String username) {
        this.message = message;
        this.username = username;
    }

}
