package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class PvChatPageController {

    private User user = null;
    private static Chat chat = null;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    private TextField Message;
    @FXML
    private ListView<Message> senderListView;
    @FXML
    private ListView<Message> receiverListView;

    @FXML

    public void setChatAndUser(Chat chat, User user) {
        PvChatPageController.chat = chat;
        this.user = user;
        messages.clear();
        messages.setAll(chat.getMessage());

        senderListView.setItems(messages);
        receiverListView.setItems(messages);

        //setupSenderCellFactory();
        //setupReceiverCellFactory();
    }


    public void sendMessage(ActionEvent event) {
        String message = Message.getText();
        chat.getMessage().add(Client.sendMessage(message, chat));
        messages.setAll(chat.getMessage());
        Message.clear();
    }


    public static void saveReceivedMessage(Message message) {
        chat.getMessage().add(message);
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
