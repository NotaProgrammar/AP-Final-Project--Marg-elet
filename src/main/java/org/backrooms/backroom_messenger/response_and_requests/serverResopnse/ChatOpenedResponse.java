package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.PvChat;

public class ChatOpenedResponse extends ServerResponse {
    @JsonProperty
    private Chat chat;

    private ObjectMapper mapper = new ObjectMapper();
    public ChatOpenedResponse(@JsonProperty("message") String message) throws JsonProcessingException {
        super(message);
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        this.chat = mapper.readValue(message,Chat.class);
    }

    public Chat getChat() {
        return chat;
    }
}
