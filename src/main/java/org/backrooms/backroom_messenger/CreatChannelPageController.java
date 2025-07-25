package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class CreatChannelPageController {

    private User user = null;
    private Channel channel = null;
    boolean publicChannel;

    @FXML
    private TextField description;
    @FXML
    private TextField name;
    @FXML
    private CheckBox publicity;
    @FXML
    private Label message;

    public void create(ActionEvent event) throws IOException {
        String channelName = name.getText();
        String channelDescription = description.getText();
        if(description.getText().isEmpty() || name.getText().isEmpty()){
            message.setTextFill(Color.RED);
            message.setText("Please Fill All Fields");
        } else {
            channel = Client.createChannel(channelName, channelDescription, publicChannel);
            channel.getUsers().add(User.changeToPrivate(user));
            channel.getRoles().add("creator");
            user.getChats().add(channel);
            goToChannelPage(event);
        }

    }


    public void setPublicityOfChannel(ActionEvent event){
        if(publicity.isSelected()){
            publicChannel = true;
        }
    }


    public void goToChannelPage(ActionEvent event) throws IOException {
        FXMLLoader channelLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChannelChatPage.fxml"));
        Scene scene = new Scene(channelLoader.load(), 900, 550);
        ChannelChatPageController ccpc = channelLoader.getController();
        ccpc.setUserAndChat(user, channel);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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


    public void setUser(User user) {
        this.user = user;
    }
}
