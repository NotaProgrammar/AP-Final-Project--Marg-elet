package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Group extends MultiUserChat{
    public String name;
    public Group(UUID id) {
        super(id);
    }

    @Override
    public String getRole(PrivateUser user) {
        return "";
    }

    @Override
    public String getName(User user) {
        return name;
    }

    @Override @JsonIgnore
    public String getType() {
        return "group";
    }
}
