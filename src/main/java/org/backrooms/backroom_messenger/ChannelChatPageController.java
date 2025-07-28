package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChannelChatPageController implements Initializable {


    private static ChannelChatPageController instance;
    private User user = null;
    private static Channel chat = null;
    boolean alreadyJoined = false;
    private ObservableList<Message> observableMessages = FXCollections.observableArrayList();


    @FXML
    private Label joinNotification;
    @FXML
    private TextField messageField;
    @FXML
    private ListView<Message> messageListView;
    @FXML
    private Label channelName;
    @FXML
    private Button joinButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Button sendButton;
    @FXML
    private Button settingButton;


    public ChannelChatPageController() {
        instance = this;
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


    public void goToSetting(ActionEvent event) throws IOException {
        FXMLLoader channelSettingLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChannelSettingPage.fxml"));
        Scene scene = new Scene(channelSettingLoader.load(), 560, 350);
        ChannelSettingPageController cspc  = channelSettingLoader.getController();
        cspc.setUserAndChannel(user, chat);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    
    @FXML
    public void sendMessage(ActionEvent event) {
        String content = messageField.getText().trim();
        if (!content.isEmpty()) {
            chat.getMessage().add(Client.sendMessage(content, chat));
            observableMessages.setAll(chat.getMessage());
            messageField.clear();
        }
    }

    @FXML
    public void joinChannel(ActionEvent event) throws IOException {
            Client.Subscribe(chat);
            if (alreadyJoined) {
                alreadyJoined = false;
                goBack(event);
            }else{
                alreadyJoined = true;
                joinNotification.setText("You have joined the channel");
                joinNotification.setTextFill(Color.GREEN);
                joinButton.setText("Leave Channel");
            }

    }


    public void setUserAndChat(User user, Channel channel) {
        this.user = user;
        this.chat = channel;

        joinButton.setDisable(false);
        joinButton.setVisible(true);
        hideMessageField(false);

        if(user.isSubed(channel)){
            alreadyJoined = true;
            joinButton.setText("Leave Channel");
            String role = channel.getRole(User.changeToPrivate(user));
            switch(role){
                case "creator":
                    joinButton.setDisable(true);
                    joinButton.setVisible(false);
                    settingButton.setText("Channel setting");
                    break;
                case "admin":
                    break;
                case "normal":
                    hideMessageField(true);
                    break;
            }
        }else{
            alreadyJoined = false;
            hideMessageField(true);
            joinButton.setText("Join Channel");
        }

        channelName.setTextFill(Color.BLUE);
        channelName.setText(chat.getName(user));

        //set list view
        observableMessages.clear();
        List<Message> messages = chat.getMessage();
        observableMessages = FXCollections.observableArrayList(messages);
        messageListView.setItems(observableMessages);


        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    setText(message.getMessage());
                }
            }
        });
    }

    private void hideMessageField(boolean bool){
        messageField.setDisable(bool);
        messageField.setVisible(!bool);

        messageLabel.setVisible(!bool);

        sendButton.setDisable(bool);
        sendButton.setVisible(!bool);
    }

    public static Chat getChat(){
        return chat;
    }

    public static void saveReceivedMessage(Message message) {
        chat.getMessage().add(message);
        instance.observableMessages.setAll(chat.getMessage());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
    }
}
