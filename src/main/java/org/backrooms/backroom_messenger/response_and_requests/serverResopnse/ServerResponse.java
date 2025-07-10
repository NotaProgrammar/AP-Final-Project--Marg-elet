package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AvailableUserResponse.class, name = "availableUserResponse"),
        @JsonSubTypes.Type(value = ChatOpenedResponse.class,name = "chatOpenedResponse"),
        @JsonSubTypes.Type(value = SearchedUsersListResponse.class , name = "searchedUsersListResponse")
})


public abstract class ServerResponse {
    @JsonProperty
    private String message;

    public ServerResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
