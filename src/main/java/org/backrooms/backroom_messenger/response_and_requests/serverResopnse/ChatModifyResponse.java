package org.backrooms.backroom_messenger.response_and_requests.serverResopnse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.PvChat;

public class ChatModifyResponse extends ServerResponse {
    @JsonProperty
    private String modification;
    @JsonProperty
    private Chat chat;
    @JsonProperty
    private String type;
    @JsonProperty
    private String role;


    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();

    public ChatModifyResponse(@JsonProperty("message") String message) {
        super(message);
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        mapper.registerSubtypes(new NamedType(MultiUserChat.class, "MultiUserChat"));
        //todo type
        try{
            String[] tokens = message.split("##");
            modification = tokens[0];
            if(tokens[0].equals("add")){
                type = tokens[1];
                switch(type){
                    case "pv_chat":
                        chat = mapper.readValue(tokens[2],PvChat.class);
                        role = tokens[3];
                        break;
                    case "muc":
                        chat = mapper.readValue(tokens[2],MultiUserChat.class);
                        role = tokens[3];
                }
            }else if(tokens[0].equals("remove")){
                type = tokens[1];
                switch(type){
                    case "pv_chat":
                        chat = mapper.readValue(tokens[2],PvChat.class);
                        break;
                    case "muc":
                        chat = mapper.readValue(tokens[2],MultiUserChat.class);
                }
            }else if(modification.equals("open")){
                type = tokens[1];
                switch(type){
                    case "pv_chat":
                        chat = mapper.readValue(tokens[2],PvChat.class);
                        role = tokens[3];
                        break;
                    case "muc":
                        chat = mapper.readValue(tokens[2],MultiUserChat.class);
                        role = tokens[3];
                        break;
                }
            } else if (modification.equals("founded")) {
                type = tokens[1];
                switch(type){
                    case "muc":
                        if(!tokens[2].equals("null")){
                            chat = mapper.readValue(tokens[2],MultiUserChat.class);
                        }
                        break;
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }

    }

    public String getModification() {
        return modification;
    }
    public String getType() {
        return type;
    }
    public String getRole() {
        return role;
    }
    public Chat getChat() {
        return chat;
    }
}
