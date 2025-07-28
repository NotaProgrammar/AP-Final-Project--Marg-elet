package org.backrooms.backroom_messenger;

import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.User;

public class ChannelSettingPageController {

    private User user = null;
    private Channel channel = null;


    public void setUserAndChannel(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
    }
}
