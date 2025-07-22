package org.backrooms.backroom_messenger;

import org.backrooms.backroom_messenger.entity.Group;
import org.backrooms.backroom_messenger.entity.User;

public class GroupChatPageController {

    private User user = null;
    private Group chat = null;


    public void setUserAndChat(User user, Group chat) {
        this.user = user;
        this.chat = chat;
    }
}
