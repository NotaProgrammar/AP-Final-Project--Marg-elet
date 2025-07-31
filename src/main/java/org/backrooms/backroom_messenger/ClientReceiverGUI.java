package org.backrooms.backroom_messenger;

import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.response_and_requests.serverResopnse.UserReadResponse;

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
                if(PvChatPageController.getChat() != null && PvChatPageController.getChat().getId().equals(message.getChat())){
                    message.setRead(true);
                    PvChatPageController.saveReceivedMessage(message);
                    Client.readMessage(message);
                }
                break;
            case "channel":
                if(ChannelChatPageController.getChat().getId().equals(message.getChat())){
                    ChannelChatPageController.saveReceivedMessage(message);
                }
                break;
            case "group":
                if(GroupChatPageController.getChat().getId().equals(message.getChat())){
                    GroupChatPageController.saveReceivedMessage(message);
                }
                break;
        }

    }

    public static void readMessage(UserReadResponse urr){
        if(PvChatPageController.getChat().getId().equals(urr.getChatId())){
            for(Message msg : PvChatPageController.getChat().getMessage()){
                if(msg.getId().equals(urr.getMsgId())){
                    msg.setRead(true);
                    PvChatPageController.refresh();
                }
            }
        }
    }

    public static void giveChatToChatPage(MultiUserChat muc, int sender){
        switch (sender){
            case 3:
                PvChatPageController.setOpenedChat(muc);
                break;
            case 4:
                ChannelChatPageController.setOpenedChat(muc);
                break;
            case 5:
                GroupChatPageController.setOpenedChat(muc);
        }

    }


}
