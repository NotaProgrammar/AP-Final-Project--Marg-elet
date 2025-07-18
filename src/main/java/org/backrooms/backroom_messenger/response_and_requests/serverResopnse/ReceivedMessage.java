package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrooms.backroom_messenger.entity.Message;

public class ReceivedMessage extends ServerResponse{
    @JsonProperty
    private Message message;

    ObjectMapper mapper = new ObjectMapper();
    public ReceivedMessage(@JsonProperty("message") String message) {
        super(message);
        try {
            this.message = mapper.readValue(message,Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public Message getMessageObject() {
        return message;
    }

}
