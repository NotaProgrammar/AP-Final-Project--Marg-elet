package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.MultiUserChat;

import org.backrooms.backroom_messenger.entity.PrivateUser;

public class NewMultiChatRequest extends ServerRequest{
    @JsonProperty
    private MultiUserChat muc;

    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();

    public NewMultiChatRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        try {
            mapper.registerSubtypes(new NamedType(MultiUserChat.class,"MultiUserChat"));
            muc = mapper.readValue(message,MultiUserChat.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public MultiUserChat getMultiUserChat(){
        return muc;
    }


}
