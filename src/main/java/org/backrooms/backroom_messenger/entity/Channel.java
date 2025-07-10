package org.backrooms.backroom_messenger.entity;

import java.util.UUID;

public class Channel extends Chat{
    public String name;
    public Channel(UUID id) {
        super(id);
    }
    @Override
    public String getName(User user) {
        return name;
    }
}
