package org.backrooms.backroom_messenger.response_and_requests.serverRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.backrooms.backroom_messenger.entity.PrivateUser;

import java.util.UUID;

public class DownloadFileRequest extends ServerRequest{
    @JsonProperty
    private UUID messageId;
    @JsonProperty
    private UUID chatId;
    @JsonProperty
    private String chatType;
    @JsonProperty
    private String saveDirectory;
    @JsonProperty
    private String fileName;


    public DownloadFileRequest(@JsonProperty("message") String message,@JsonProperty("sender") PrivateUser sender) {
        super(message, sender);
        String[] tokens = message.split("##");
        this.messageId = UUID.fromString(tokens[0]);
        this.chatId = UUID.fromString(tokens[1]);
        this.chatType = tokens[2];
        this.saveDirectory = tokens[3];
        this.fileName = tokens[4];
    }

    public UUID getMessageId() {
        return messageId;
    }

    public UUID getChatId() {
        return chatId;
    }
    public String getChatType() {
        return chatType;
    }
    public String getSaveDirectory() {
        return saveDirectory;
    }
    public String getFileName() {
        return fileName;
    }
}
