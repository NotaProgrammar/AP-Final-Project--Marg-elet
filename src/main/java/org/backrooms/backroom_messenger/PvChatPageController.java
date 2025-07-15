package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;
import org.w3c.dom.Text;

import java.io.IOException;

public class PvChatPageController {

    private User user = null;
    private Chat chat = null;

    @FXML
    private TextField Message;

    @FXML

    public void setChatAndUser(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
    }


    public void sendMessage(ActionEvent event) {
        String message = Message.getText();
        chat.getMessage().add(Client.sendMessage(message, chat));
    }


    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader displayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(displayLoader.load(), 560, 350);
        MainDisplayController mdc  = displayLoader.getController();
        mdc.setUser(this.user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
