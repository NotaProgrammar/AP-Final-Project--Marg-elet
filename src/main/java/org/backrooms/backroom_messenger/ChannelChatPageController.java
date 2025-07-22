package org.backrooms.backroom_messenger;

import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

public class ChannelChatPageController {

    private User user = null;
    private Channel chat = null;


    public void setUserAndChat(User user, Channel chat) {
        this.user = user;
        this.chat = chat;
    }
}
