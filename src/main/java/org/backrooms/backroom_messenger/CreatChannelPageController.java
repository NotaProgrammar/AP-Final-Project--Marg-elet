package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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
    private RadioButton publicity;
    @FXML
    private RadioButton privateChannel;


    public void create(ActionEvent event){
        String channelName = name.getText();
        String channelDescription = description.getText();
        if(description.getText().isEmpty() ){}
        channel = Client.createChannel(channelName, channelDescription, publicChannel);
        user.getChats().add(channel);
    }


    public void setPublicityOfChannel(ActionEvent event){
        if(publicity.isSelected()){
            publicChannel = true;
        }
        else if(privateChannel.isSelected()){
            publicChannel = false;
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


    public void setUser(User user) {
        this.user = user;
    }
}
