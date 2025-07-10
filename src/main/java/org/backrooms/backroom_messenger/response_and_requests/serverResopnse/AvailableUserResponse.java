package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.PrivateUser;
import org.backrooms.backroom_messenger.entity.PvChat;
import org.backrooms.backroom_messenger.entity.User;

import java.util.List;


public class AvailableUserResponse extends ServerResponse {
    @JsonProperty
    private boolean userFound = false;
    private User user = null;

    ObjectMapper mapper = new ObjectMapper();

    public AvailableUserResponse(@JsonProperty("message") String message) {
        super(message);
    }

    public boolean isUserFound() {
        this.getUser();

        if(user != null) {
            this.userFound = true;
        }else {
            this.userFound = false;
        }
        return userFound;
    }

    public User getUser() {
        try {
            mapper.registerSubtypes(new NamedType(PvChat.class,"PvChat"));
            this.user = mapper.readValue(super.getMessage(), User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
