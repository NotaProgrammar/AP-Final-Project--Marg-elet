package org.backrooms.backroom_messenger;

import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;

import java.util.List;

public class ClientReceiverGUI {

    public static void searchResult(List<Chat> chats){
        MainDisplayController.searchResult(chats);
    }


    public static void openPvChat(Chat chat){
        MainDisplayController.pvChatResult(chat);
    }


    public static void openChatInSearch(Chat chat){
        SearchPageController.openChatInSearchResult(chat);
    }


    public static void addReceivedMessage(Message message){
        switch(message.getChatType()){
            case "pv_chat":
                if(PvChatPageController.getChat().getUser(PvChatPageController.getUser()).getUsername().equals(message.getSender())){
                    message.setRead(true);
                    PvChatPageController.saveReceivedMessage(message);
                    Client.readMessage(message);
                }
                break;
            case "channel":
                if(ChannelChatPageController.getChat().getId().equals(message.getChat())){
                    ChannelChatPageController.saveReceivedMessage(message);
                }
        }

    }
}
