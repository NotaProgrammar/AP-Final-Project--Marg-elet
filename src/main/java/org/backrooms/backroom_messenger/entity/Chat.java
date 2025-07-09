package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.AvailableUserResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.SearchedUsersListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "jsonType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PvChat.class, name="pvChat")
})

public abstract class Chat {
    @JsonProperty
    private UUID id;
    private List<Message> message = new ArrayList<>();
    @JsonProperty
    private String type;



    public Chat(@JsonProperty("id")UUID id,@JsonProperty("type") String type) {
        this.id = id;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public List<Message> getMessage() {
        return message;
    }

    public abstract String getName(User user);

    public String getType() {
        return type;
    }
}
