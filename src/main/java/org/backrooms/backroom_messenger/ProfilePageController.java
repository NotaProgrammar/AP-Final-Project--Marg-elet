package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.PvChat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class ProfilePageController {

    private User user = null;
    private PvChat chat = null;

    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label bioLabel;
    @FXML
    private Label birthdayLabel;


    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader pvChatLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("PvChatPage.fxml"));
        Scene scene = new Scene(pvChatLoader.load(), 560, 350);
        PvChatPageController pcpc  = pvChatLoader.getController();
        pcpc.setChatAndUser(chat, user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void setUserAndChat(User user, PvChat chat) {
        this.user = user;
        this.chat = chat;

        nameLabel.setText(user.getName());
        usernameLabel.setText(user.getUsername());
        bioLabel.setText(user.getBio());
        birthdayLabel.setText(user.getDateOfBirth().toString());
    }
}
