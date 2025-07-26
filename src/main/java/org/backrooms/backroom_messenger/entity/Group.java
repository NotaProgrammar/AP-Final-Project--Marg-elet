package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Group extends Chat{
    public String name;
    public Group(UUID id) {
        super(id);
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
