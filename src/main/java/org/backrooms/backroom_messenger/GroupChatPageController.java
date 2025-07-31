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
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GroupChatPageController {

    private static Lock lock = new ReentrantLock();
    private static MultiUserChat opened = null;
    private static boolean isChannelOpened = false;
    private static User user = null;
    private static MultiUserChat chat = null;
    private boolean alreadyJoined = false;
    private static ObservableList<Message> observableMessages = FXCollections.observableArrayList();

    @FXML
    private TextField messageField;
    @FXML
    private Label joinNotif;
    @FXML
    private ListView<Message> groupMessages;
    @FXML
    private Button joinButton;
    @FXML
    private Label groupName;
    @FXML
    private Button sendButton;
    @FXML
    private Button settingButton;



    public void setUserAndChat(User loggedUser, MultiUserChat openedChat) {
        user = loggedUser;
        chat = openedChat;

        groupName.setTextFill(Color.BLUE);
        groupName.setText(chat.getName(user));

        joinButton.setDisable(false);
        joinButton.setVisible(true);
        hideMessageField(false);

        if(user.isSubed(chat))
        {
            alreadyJoined = true;
            joinButton.setText("leave");
            String role = chat.getRole(User.changeToPrivate(user));
            switch(role){
                case "creator":
                    joinButton.setDisable(true);
                    joinButton.setVisible(false);
                    break;
                case "admin":
                    break;
                case "normal":
                    hideMessageField(true);
                    break;
            }
        }else{
            alreadyJoined = false;
            joinButton.setText("Join");
            hideMessageField(true);
        }

        List<Message> messageList = new ArrayList<>();
        messageList.addAll(chat.getMessage());
        messageList.sort(Comparator.comparing(Message::getDate));
        observableMessages.clear();
        observableMessages.setAll(messageList);
        setupCellFactories();
    }


    public void setupCellFactories(){
        groupMessages.setItems(observableMessages);

        groupMessages.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    Label messageLabel = new Label(message.getSender() + " : " + message.toString(user.getUsername()));

                    Button chatButton = new Button("Open Chat");
                    chatButton.setOnAction(e -> {
                        openChat(e,message.getLinkToMultiUserChat());
                    });

                    // Layout for the cell content
                    HBox cellBox = new HBox(10);
                    cellBox.setAlignment(message.getSender().equals(user.getUsername())
                            ? Pos.CENTER_RIGHT
                            : Pos.CENTER_LEFT);

                    if (message.getLinkToMultiUserChat() != null) {
                        messageLabel.setText(message.getSender() + " : " + message.getLinkToMultiUserChat().getName(null));
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

    private void openChat(ActionEvent event, MultiUserChat chat) {
        try {
            isChannelOpened = false;
            opened = null;
            if(chat.isChannel()){
                goToChannelPage(event,chat);
            }else{
                goToGroupPage(event,chat);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void goToGroupPage(ActionEvent event, MultiUserChat chat) throws InterruptedException {
        Client.openChat(chat, 5);
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

    private void hideMessageField(boolean bool){
        messageField.setDisable(bool);
        messageField.setVisible(!bool);


        sendButton.setDisable(bool);
        sendButton.setVisible(!bool);
    }

    private void goToChannelPage(ActionEvent event,MultiUserChat selected) throws InterruptedException {
        Client.openChat(selected, 5);
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

    public static void setOpenedChat(MultiUserChat chat) {
        opened = chat;
        isChannelOpened = true;
    }

    public static void saveReceivedMessage(Message message) {
        try{
            chat.getMessage().add(message);
            lock.lock();
            observableMessages.clear();
            observableMessages.addAll(chat.getMessage());
            lock.unlock();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static MultiUserChat getChat() {
        return chat;
    }


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
    public void joinGroup(ActionEvent event) throws IOException {
        Client.Subscribe(chat);
        if (alreadyJoined) {
            alreadyJoined = false;
            goBack(event);
        }else{
            alreadyJoined = true;
            joinNotif.setText("you joined");
            joinNotif.setTextFill(Color.GREEN);
            joinButton.setText("left");
        }
    }

    @FXML
    public void goToSettingPage(ActionEvent event) throws IOException {
        FXMLLoader groupSettingLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("GroupSettingPage.fxml"));
        Scene scene = new Scene(groupSettingLoader.load(), 560, 350);
        GroupSettingPageController cspc  = groupSettingLoader.getController();
        cspc.setUserAndGroup(user, chat);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
