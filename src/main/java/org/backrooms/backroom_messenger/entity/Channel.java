package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Channel extends Chat{
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private HashMap<PrivateUser,String> users = new HashMap<>();
    @JsonProperty
    private String creator;
    @JsonProperty
    private boolean publicity;


    public Channel(@JsonProperty("id") UUID id,
                   @JsonProperty("name") String name,
                   @JsonProperty("description") String description,
                   @JsonProperty("publicity") boolean publicity,
                   @JsonProperty("creator") String creator) {
        super(id);
        this.name = name;
        this.description = description;
        this.publicity = publicity;
        this.creator = creator;
    }


    @Override
    public String getName(User user) {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<PrivateUser, String> getUsers() {
        return users;
    }

    public boolean getPublicity() {
        return publicity;
    }

    public String getCreator(){
        return creator;
    }

}
