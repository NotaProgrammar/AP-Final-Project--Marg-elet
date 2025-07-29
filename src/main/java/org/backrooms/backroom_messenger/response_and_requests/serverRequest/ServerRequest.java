package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.backrooms.backroom_messenger.entity.PrivateUser;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = "loginRequest"),
        @JsonSubTypes.Type(value = SignupRequest.class, name = "signupRequest"),
        @JsonSubTypes.Type(value = SearchRequest.class, name = "searchRequest"),
        @JsonSubTypes.Type(value = NewChatRequest.class, name = "newChatRequest"),
        @JsonSubTypes.Type(value = SendMessageRequest.class, name = "sendMessageRequest"),
        @JsonSubTypes.Type(value = NewChannelRequest.class, name = "newChannelRequest"),
        @JsonSubTypes.Type(value = OpenChannelRequest.class, name = "openChannelRequest"),
        @JsonSubTypes.Type(value = SubRequest.class, name = "subRequest"),
        @JsonSubTypes.Type(value = ChangePropertyRequest.class, name = "changePropertyRequest"),
        @JsonSubTypes.Type(value = ChangeRoleRequest.class, name = "changeRoleRequest"),
        @JsonSubTypes.Type(value = RemoveUserRequest.class, name = "removeUserRequest"),
        @JsonSubTypes.Type(value = SignOutRequest.class, name = "signOutRequest"),
        @JsonSubTypes.Type(value = ChatReadRequest.class,name = "chatReadRequest")
})

public abstract class ServerRequest {

    @JsonProperty
    private String message;
    @JsonProperty
    private PrivateUser sender;

    public ServerRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        this.message = message;
        this.sender = sender;
    }

    public PrivateUser getSender() {
        return sender;
    }

}
