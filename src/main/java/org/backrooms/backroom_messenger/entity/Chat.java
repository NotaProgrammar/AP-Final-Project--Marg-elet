package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PvChat.class, name="PvChat"),
        @JsonSubTypes.Type(value = Channel.class, name = "channel")
})

public abstract class Chat {
    @JsonProperty
    private UUID id;
    private List<Message> message = new ArrayList<>();


    public Chat(@JsonProperty("id")UUID id) {
        this.id = id;

    }

    public UUID getId() {
        return id;
    }

    public List<Message> getMessage() {
        return message;
    }

    public abstract String getName(User user);

    @JsonIgnore
    public abstract String getType();
}
