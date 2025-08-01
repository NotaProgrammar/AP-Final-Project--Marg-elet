package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

public class SetImageRequest extends ServerRequest{
    @JsonProperty
    private String imageBase64;

    public SetImageRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        imageBase64 = message;
    }

    public String getImageBase64() {
        return imageBase64;
    }
}
