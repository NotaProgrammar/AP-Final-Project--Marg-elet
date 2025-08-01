package org.backrooms.backroom_messenger.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.ClientReceiverGUI;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.PvChat;
import org.backrooms.backroom_messenger.response_and_requests.serverRequest.ServerRequest;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.*;
import org.backrooms.backroom_messenger.server.Server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

import static org.backrooms.backroom_messenger.client.Client.*;

public class ClientReceiver implements Runnable {
    private BufferedReader reader;
    private ObjectMapper mapper = new ObjectMapper();

    public ClientReceiver(BufferedReader reader) {
        this.reader = reader;
        registerMapper();
    }

    private void registerMapper() {
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
        mapper.registerSubtypes(new NamedType(MultiUserChat.class,"channel"));
    }

    @Override
    public void run() {
        try {
            while(true){
                StringBuilder sb = new StringBuilder();
                String endMarker = "###END###";

                char[] buffer = new char[1024];
                int charsRead;

                while ((charsRead = reader.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, charsRead);
                    sb.append(chunk);
                    if (sb.toString().contains(endMarker)) {
                        break;
                    }
                }

                String json = sb.toString().replace(endMarker, "");
                ServerResponse serverResponse = mapper.readValue(json, ServerResponse.class);
                Thread thread = new Thread(() -> {
                    responseCheck(serverResponse);
                });
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void responseCheck(ServerResponse sr){
        if(sr instanceof SearchedUsersListResponse sulr){
            userListHandle(sulr);
        }else if(sr instanceof ChatModifyResponse cmr){
            Client.chatModifyHandle(cmr);
        }else if(sr instanceof ReceivedMessage rm){
                ClientReceiverGUI.addReceivedMessage(rm.getMessageObject());
        }else if(sr instanceof UserLogResponse ulor){
            Client.setLastSeen(ulor);
        }else if(sr instanceof UserReadResponse urr){
            ClientReceiverGUI.readMessage(urr);
        }
    }
}
