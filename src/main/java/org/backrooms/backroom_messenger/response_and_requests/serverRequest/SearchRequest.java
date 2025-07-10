package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

public class SearchRequest extends ServerRequest{
    @JsonProperty
    private String searchTerm;

    public SearchRequest(@JsonProperty("message") String message,@JsonProperty("username") PrivateUser username) {
        super(message, username);
        this.searchTerm = message;
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
