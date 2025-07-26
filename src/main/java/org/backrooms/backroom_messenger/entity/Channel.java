package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Channel extends Chat{
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private List<PrivateUser> users = new ArrayList<>();
    @JsonProperty
    private List<String> roles = new ArrayList<>();
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

    @Override @JsonIgnore
    public String getType() {
        return "channel";
    }

    public String getDescription() {
        return description;
    }

    public String getRole(PrivateUser user){
        for(int i=0; i<users.size(); i++){
            if(users.get(i).getUsername().equals(user.getUsername())){
                return roles.get(i);
            }
        }
        return null;
    }

    public List<PrivateUser> getUsers() {
        return users;
    }

    public List<String> getRoles() {
        return roles;
    }


    public boolean getPublicity() {
        return publicity;
    }

    public String getCreator(){
        return creator;
    }

}
