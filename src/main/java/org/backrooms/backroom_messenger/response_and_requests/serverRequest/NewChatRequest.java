package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrooms.backroom_messenger.entity.User;


public class NewChatRequest extends ServerRequest{
    @JsonProperty
    User user;
    ObjectMapper mapper = new ObjectMapper();
    public NewChatRequest(@JsonProperty("message") String message,@JsonProperty("username") String username) {
        super(message, username);
        try {
            this.user = mapper.readValue(message, User.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }

    public User getUser() {
        return user;
    }
}
