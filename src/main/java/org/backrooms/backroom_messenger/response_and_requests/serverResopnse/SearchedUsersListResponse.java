package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;

import org.backrooms.backroom_messenger.entity.PvChat;

import java.util.ArrayList;
import java.util.List;

public class SearchedUsersListResponse extends ServerResponse {
    private List<Chat> chats = new ArrayList<>();

    @JsonIgnore
    ObjectMapper mapper = new ObjectMapper();

    public SearchedUsersListResponse(@JsonProperty("message") String message) {
        super(message);
        try {
            mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
            mapper.registerSubtypes(new NamedType(Channel.class, "channel"));
            this.chats.addAll(mapper.readValue(super.getMessage(), new TypeReference<List<Chat>>() {}));
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }

    }

    public List<Chat> getChats() {
        return chats;
    }
}
