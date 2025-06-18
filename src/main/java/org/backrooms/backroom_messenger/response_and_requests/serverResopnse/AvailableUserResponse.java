package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrooms.backroom_messenger.entity.User;





public class AvailableUserResponse extends ServerResponse {
    @JsonProperty
    private boolean userFound;
    @JsonProperty
    private User user;

    ObjectMapper mapper = new ObjectMapper();
    public AvailableUserResponse(@JsonProperty("message") String message) {
        super(message);
        try {
            this.user = mapper.readValue(message, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if(user != null) {
            this.userFound = true;
        }else {
            this.userFound = false;
        }

    }

    public boolean isUserFound() {
        return userFound;
    }

    public User getUser() {
        return user;
    }
}
