package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class ChannelChatPageController {

    private User user = null;
    private Channel chat = null;

    @FXML
    private Label joinNotification;


    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader displayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(displayLoader.load(), 560, 350);
        MainDisplayController mdc  = displayLoader.getController();
        mdc.setUser(this.user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void joinChannel(ActionEvent event) {
        //todo : boolean alreadyJoined = یه تابع توی بک باشه که یوزر رو بهش بدم و بگرده اگر یوزر توی چنل بوده
        boolean alreadyJoined = user.isSubed(chat);

        if(alreadyJoined == false) {
            joinNotification.setTextFill(Color.GREEN);
            joinNotification.setText("you joined the Channel");
            //todo : get the created chat for user to add in channel
            //todo : همون که وقتی جوین داد تو چنل به لیست چت هاش اضافه بشه

        }
    }


    public void setUserAndChat(User user, Channel chat) {
        this.user = user;
        this.chat = chat;
    }
}
