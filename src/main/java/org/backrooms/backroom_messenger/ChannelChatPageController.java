package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Channel;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ChannelChatPageController {


    private static User user = null;
    private static Channel chat = null;
    boolean alreadyJoined = false;
    private static ObservableList<Message> observableMessages = FXCollections.observableArrayList();
    private static Channel opened = null;
    private static boolean isChannelOpened = false;


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



    public void goBack(ActionEvent event) throws IOException {
        chat = null;
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
        messages.sort(Comparator.comparing(Message::getDate));
        observableMessages = FXCollections.observableArrayList(messages);
        messageListView.setItems(observableMessages);


        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    Label messageLabel = new Label(message.toString(user.getUsername()));

                    Button chatButton = new Button("Open Chat");
                    chatButton.setOnAction(e -> {
                        openChat(e,message.getLinkToChannel());
                    });

                    // Layout for the cell content
                    HBox cellBox = new HBox(10);
                    messageLabel.setAlignment(Pos.CENTER);

                    if (message.getLinkToChannel() != null) {
                        messageLabel.setText(message.getLinkToChannel().getName(null));
                        cellBox.getChildren().addAll(messageLabel, chatButton);
                    } else {
                        cellBox.getChildren().add(messageLabel);
                    }

                    setText(null);
                    setGraphic(cellBox);
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
        observableMessages.setAll(chat.getMessage());
    }
    private void openChat(ActionEvent event, Channel chat) {
        try {
            isChannelOpened = false;
            opened = null;
            goToChannelPage(event,chat);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void goToChannelPage(ActionEvent event,Channel selected) throws InterruptedException {
        Client.openChat(selected, 4);
        while(!isChannelOpened){
            Thread.sleep(100);
        }
        try{
            FXMLLoader channelLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChannelChatPage.fxml"));
            Scene scene = new Scene(channelLoader.load(), 900, 550);
            ChannelChatPageController ccpc = channelLoader.getController();
            ccpc.setUserAndChat(user, opened);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void setOpenedChat(Channel chat) {
        opened = chat;
        isChannelOpened = true;
    }

}
