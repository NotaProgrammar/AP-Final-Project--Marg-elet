package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = User.class, name = "user")
})

public class PrivateUser {
    @JsonProperty
    private String username;
    @JsonProperty
    private String name;
    //todo status
    //todo profile

    public PrivateUser(@JsonProperty("username") String username,@JsonProperty("name") String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}
