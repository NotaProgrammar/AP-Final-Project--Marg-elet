package org.backrooms.backroom_messenger.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.backrooms.backroom_messenger.ClientReceiverGUI;
import org.backrooms.backroom_messenger.entity.PvChat;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ChatOpenedResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ReceivedMessage;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.SearchedUsersListResponse;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.ServerResponse;

import java.io.DataInputStream;
import java.io.IOException;

import static org.backrooms.backroom_messenger.client.Client.*;

public class ClientReceiver implements Runnable {
    private DataInputStream dis;
    private ObjectMapper mapper = new ObjectMapper();

    public ClientReceiver(DataInputStream dis) {
        this.dis = dis;
        registerMapper();
    }

    private void registerMapper() {
        mapper.registerSubtypes(new NamedType(PvChat.class, "PvChat"));
    }

    @Override
    public void run() {
        try {
            while(true){
                String response = dis.readUTF();
                ServerResponse serverResponse = mapper.readValue(response,ServerResponse.class);
                responseCheck(serverResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void responseCheck(ServerResponse sr){
        if(sr instanceof SearchedUsersListResponse sulr){
            userListHandle(sulr);
        }else if(sr instanceof ChatOpenedResponse cor){
            openChat(cor);
        }else if(sr instanceof ReceivedMessage rm){
            ClientReceiverGUI.addReceivedMessage(rm.getMessageObject());
        }
    }
}
