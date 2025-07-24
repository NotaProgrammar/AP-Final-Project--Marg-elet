package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.PrivateUser;


public class SubRequest extends ServerRequest{
    @JsonProperty
    private Channel channel;

    ObjectMapper mapper = new ObjectMapper();

    public SubRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        try {
            mapper.registerSubtypes(new NamedType(Channel.class, "channel"));
            channel = mapper.readValue(message, Channel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
