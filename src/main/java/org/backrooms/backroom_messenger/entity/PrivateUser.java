package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;



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
