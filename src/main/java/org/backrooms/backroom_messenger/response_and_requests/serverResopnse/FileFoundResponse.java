package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileFoundResponse extends ServerResponse{
    @JsonProperty
    private String directory;
    @JsonProperty
    private String fileBase64;
    @JsonProperty
    private String fileName;


    public FileFoundResponse(@JsonProperty("message") String message) {
        super(message);
        String[] tokens = message.split("##");
        this.directory = tokens[0];
        this.fileBase64 = tokens[1];
        this.fileName = tokens[2];
    }

    public String getDirectory() {
        return directory;
    }
    public String getFileBase64() {
        return fileBase64;
    }
    public String getFileName() {
        return fileName;
    }
}
