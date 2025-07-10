package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.ArrayList;
import java.util.List;

public class SearchedUsersListResponse extends ServerResponse {
    private List<PrivateUser> users = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();

    public SearchedUsersListResponse(@JsonProperty("message") String message) {
        super(message);
        try {
            this.users.addAll(mapper.readValue(super.getMessage(), new TypeReference<List<PrivateUser>>() {}));
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

    }

    public List<PrivateUser> getUsers() {
        return users;
    }
}
