package org.backrooms.backroom_messenger;

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


    public static void addReceivedMessage(Message message){
        if(PvChatPageController.getChat().getUserName(PvChatPageController.getUser()).equals(message.getSender())){
            PvChatPageController.saveReceivedMessage(message);
        }
    }
}
