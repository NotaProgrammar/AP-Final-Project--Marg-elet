package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class SetImageRequest extends ServerRequest{
    @JsonProperty
    private String imageBase64;
    @JsonProperty
    private String type;
    @JsonProperty
    private UUID id;

    public SetImageRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("###");
        imageBase64 = tokens[0];
        type = tokens[1];
        if(type.equals("muc")){
            id = UUID.fromString(tokens[2]);
        }
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public String getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }
}
