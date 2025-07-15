package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.PrivateUser;
import org.backrooms.backroom_messenger.entity.PvChat;

public class SendMessageRequest extends ServerRequest{
    @JsonProperty
    private Message sendedMessage;

    private ObjectMapper mapper = new ObjectMapper();

    public SendMessageRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        try {
            sendedMessage = mapper.readValue(message,Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Message getSendedMessage() {
        return sendedMessage;
    }
}
