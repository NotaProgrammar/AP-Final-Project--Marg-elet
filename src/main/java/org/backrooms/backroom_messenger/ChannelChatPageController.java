package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

public class ChannelChatPageController {

    private User user = null;
    private Channel chat = null;

    @FXML
    private Label joinNotification;


    public void joinChannel(ActionEvent event) {
        //todo : boolean alreadyJoined = یه تابع توی بک باشه که یوزر رو بهش بدم و بگرده اگر یوزر توی چنل بوده
        boolean alreadyJoined = false;

        if(alreadyJoined == false) {
            joinNotification.setTextFill(Color.GREEN);
            joinNotification.setText("you joined the Channel");
            //todo : get the created chat for user to add in channel
            //todo : همون که وقتی جوین داد تو چنل به لیست چت هاش اضافه بشه
            User.getChats().add()
        }
    }


    public void setUserAndChat(User user, Channel chat) {
        this.user = user;
        this.chat = chat;
    }
}
