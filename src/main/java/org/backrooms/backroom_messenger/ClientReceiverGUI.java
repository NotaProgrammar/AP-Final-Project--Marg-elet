package org.backrooms.backroom_messenger;

import org.backrooms.backroom_messenger.entity.Chat;

import java.util.List;

public class ClientReceiverGUI {

    public static void searchResult(List<Chat> chats){
        MainDisplayController.searchResult(chats);
    }

    public static void openPvChat(Chat chat){
        MainDisplayController.pvChatResult(chat);
    }
}
