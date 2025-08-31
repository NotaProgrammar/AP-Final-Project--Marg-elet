package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.PrivateUser;
import org.backrooms.backroom_messenger.entity.PvChat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ProfilePageController {

    private PrivateUser user = null;
    private PvChat chat = null;
    private User currentUser;

    @FXML
    private AnchorPane profilePane;
    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label bioLabel;
    @FXML
    private ImageView profileImage;


    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader pvChatLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("PvChatPage.fxml"));
        Scene scene = new Scene(pvChatLoader.load(), 560, 350);
        PvChatPageController pcpc  = pvChatLoader.getController();
        pcpc.setChatAndUser(chat, currentUser);
        pcpc.setupCellFactories();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void setUserAndChat(User currentUser, PrivateUser user, PvChat chat) {
        this.currentUser = currentUser;
        this.user = user;
        this.chat = chat;

        String imageBase64 = user.getImageBase64();
        if(imageBase64 != null){
            byte[] bytes = Base64.getDecoder().decode(imageBase64);
            profileImage.setImage(new Image(new ByteArrayInputStream(bytes)));
        }
        nameLabel.setText(user.getName());
        usernameLabel.setText(user.getUsername());
        if(user.getBio() != null){
            bioLabel.setText(user.getBio());
        }
    }
}
