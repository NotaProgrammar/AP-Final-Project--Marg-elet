package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class CreatChannelPageController {

    private User user = null;
    private Channel channel = null;

    @FXML
    private TextField description;
    @FXML
    private TextField name;


    public void creat(ActionEvent event){
        String channelName = name.getText();
        String channelDescription = description.getText();
    }


    public void setUser(User user) {
        this.user = user;
    }
}
