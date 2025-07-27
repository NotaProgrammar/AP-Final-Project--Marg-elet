package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Group;
import org.backrooms.backroom_messenger.entity.Message;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.util.List;

public class GroupChatPageController {

    private User user = null;
    private Group chat = null;
    private boolean alreadyJoined = false;
    private ObservableList<Message> observableMessages = FXCollections.observableArrayList();

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


    public void setUserAndChat(User user, Group chat) {
        this.user = user;
        this.chat = chat;

        groupName.setTextFill(Color.BLUE);
        groupName.setText(chat.getName(user));

        joinButton.setDisable(false);
        joinButton.setVisible(true);
        if(user.isSubed(chat)) // todo : برای بررسی عضویت فقط چنل میگیره و گروه نمیگیره
        {
            alreadyJoined = true;
            joinButton.setText("left");
            String role = chat.getRole(User.changeToPrivate(user));
            switch(role){
                case "creator":
                    joinButton.setDisable(true);
                    joinButton.setVisible(false);
                    break;
                case "admin":
                    break;
                case "normal":
                    break;
            }
        }else{
            alreadyJoined = false;
            joinButton.setText("Join");
        }

        observableMessages.clear();
        observableMessages.setAll(chat.getMessage());
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


    public void goBack(ActionEvent event) throws IOException {
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
            observableMessages.setAll(chat.getMessage());
            messageField.clear();
        }
    }


    @FXML
    public void joinGroup(ActionEvent event) throws IOException {
        Client.Subscribe(chat);  // todo : این باید درست بشه، ورودی از جنس چنل باید باشه ولی من گروه دارم
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
}
