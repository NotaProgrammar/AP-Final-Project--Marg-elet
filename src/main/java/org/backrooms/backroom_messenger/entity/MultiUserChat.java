package org.backrooms.backroom_messenger.entity;

import java.util.UUID;

public abstract class MultiUserChat extends Chat{

    public MultiUserChat(UUID id) {
        super(id);
    }

    public abstract String getRole(PrivateUser user);

}
