package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelChatPageController {

    private static Lock lock = new ReentrantLock();
    private static User user = null;
    private static MultiUserChat chat = null;
    boolean alreadyJoined = false;
    private static ObservableList<Message> observableMessages = FXCollections.observableArrayList();
    private static MultiUserChat opened = null;
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
            lock.lock();
            observableMessages.clear();
            observableMessages.setAll(chat.getMessage());
            lock.unlock();
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


    public void setUserAndChat(User user, MultiUserChat muc) {
        this.user = user;
        this.chat = muc;

        joinButton.setDisable(false);
        joinButton.setVisible(true);
        hideMessageField(false);

        if(user.isSubed(muc)){
            alreadyJoined = true;
            joinButton.setText("Leave Channel");
            String role = muc.getRole(User.changeToPrivate(user));
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
                        openChat(e,message.getLinkToMultiUserChat());
                    });

                    // Layout for the cell content
                    HBox cellBox = new HBox(10);
                    messageLabel.setAlignment(Pos.CENTER);

                    if (message.getLinkToMultiUserChat() != null) {
                        messageLabel.setText(message.getLinkToMultiUserChat().getName(null));
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
        lock.lock();
        observableMessages.clear();
        observableMessages.setAll(chat.getMessage());
        lock.unlock();
    }

    private void openChat(ActionEvent event, MultiUserChat muc) {
        try {
            isChannelOpened = false;
            opened = null;
            if(muc.isChannel()){
                goToChannelPage(event,muc);
            }else{
                goToGroupPage(event,muc);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void goToGroupPage(ActionEvent event, MultiUserChat muc) throws InterruptedException {
        Client.openChat(muc, 4);
        while(!isChannelOpened){
            Thread.sleep(100);
        }
        try{
            FXMLLoader groupLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("GroupChatPage.fxml"));
            Scene scene = new Scene(groupLoader.load(), 900, 550);
            GroupChatPageController gcpc = groupLoader.getController();
            gcpc.setUserAndChat(user, opened);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void goToChannelPage(ActionEvent event,MultiUserChat selected) throws InterruptedException {
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

    public static void setOpenedChat(MultiUserChat   chat) {
        opened = chat;
        isChannelOpened = true;
    }

}
