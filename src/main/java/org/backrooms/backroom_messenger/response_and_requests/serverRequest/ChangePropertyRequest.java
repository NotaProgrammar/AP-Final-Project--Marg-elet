package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class ChangePropertyRequest extends ServerRequest{
    @JsonProperty
    private String newProperty;
    @JsonProperty
    private UUID id;
    @JsonProperty
    private String property;



    public ChangePropertyRequest(@JsonProperty("message") String message, @JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        this.id = UUID.fromString(tokens[0]);
        this.property = tokens[1];
        this.newProperty = tokens[2];
    }

    public String getNewProperty() {
        return newProperty;
    }

    public UUID getId() {
        return id;
    }
    public String getProperty() {
        return property;
    }

}
