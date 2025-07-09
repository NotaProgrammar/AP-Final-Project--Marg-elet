package org.backrooms.backroom_messenger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PvChat extends Chat{
    @JsonProperty
    private PrivateUser user1;
    @JsonProperty
    private PrivateUser user2;

    public PvChat(@JsonProperty("id") UUID id,@JsonProperty("user1") PrivateUser user1,@JsonProperty("user2") PrivateUser user2) {
        super(id,"pv_chat");
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public String getName(User user) {
        if(user1.getUsername().equals(user.getUsername())){
            return user2.getName();
        }else{
            return user1.getName();
        }
    }

}
