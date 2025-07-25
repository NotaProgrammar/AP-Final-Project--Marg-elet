package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.*;

import java.io.IOException;


public class PvChatPageController {

    private static PvChatPageController instance;
    private static User user = null;
    private static PvChat chat = null;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    private TextField Message;
    @FXML
    private ListView<Message> MessageListView;

    public PvChatPageController() {
        instance = this;
    }


    @FXML
    public void initialize() {
        instance = this;
    }


    public void setChatAndUser(PvChat chat, User user) {
        PvChatPageController.chat = chat;
        PvChatPageController.user = user;
        messages.clear();
        messages.setAll(chat.getMessage());
    }


    public void setupCellFactories() {
        MessageListView.setItems(messages);


        MessageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    setText(message.getMessage());
                    if (message.getSender().equals(user.getUsername())) {
                        setStyle("-fx-alignment: center-right;");
                    } else {
                        setStyle("-fx-alignment: center-left;");
                    }
                }

            }
        });

    }


    public void sendMessage(ActionEvent event) {
        String message = Message.getText().trim();
        chat.getMessage().add(Client.sendMessage(message, chat));
        messages.setAll(chat.getMessage());
        Message.clear();
    }


    public static void saveReceivedMessage(Message message) {
        chat.getMessage().add(message);
        instance.messages.setAll(chat.getMessage());
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


    public static PvChatPageController getInstance() {
        return instance;
    }

    public static User getUser() {
        return user;
    }

    public static PvChat getChat() {
        return chat;
    }

}
