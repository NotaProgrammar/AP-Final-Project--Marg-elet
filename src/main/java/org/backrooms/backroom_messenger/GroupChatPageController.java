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

public class GroupChatPageController {

    private static MultiUserChat opened = null;
    private static boolean isChannelOpened = false;
    private static User user = null;
    private static MultiUserChat chat = null;
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


    public void setUserAndChat(User user, MultiUserChat chat) {
        this.user = user;
        this.chat = chat;

        groupName.setTextFill(Color.BLUE);
        groupName.setText(chat.getName(user));

        joinButton.setDisable(false);
        joinButton.setVisible(true);
//        if(user.isSubed(chat)) // todo : برای بررسی عضویت فقط چنل میگیره و گروه نمیگیره
//        {
//            alreadyJoined = true;
//            joinButton.setText("leave");
//            String role = chat.getRole(User.changeToPrivate(user));
//            switch(role){
//                case "creator":
//                    joinButton.setDisable(true);
//                    joinButton.setVisible(false);
//                    break;
//                case "admin":
//                    break;
//                case "normal":
//                    break;
//            }
//        }else{
//            alreadyJoined = false;
//            joinButton.setText("Join");
//        }

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
                    Label messageLabel = new Label(message.toString(user.getUsername()));

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

    private void openChat(ActionEvent event, MultiUserChat chat) {
        try {
            isChannelOpened = false;
            opened = null;
            goToChannelPage(event,chat);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
//        Client.Subscribe(chat);  // todo : این باید درست بشه، ورودی از جنس چنل باید باشه ولی من گروه دارم
//        if (alreadyJoined) {
//            alreadyJoined = false;
//            goBack(event);
//        }else{
//            alreadyJoined = true;
//            joinNotif.setText("you joined");
//            joinNotif.setTextFill(Color.GREEN);
//            joinButton.setText("left");
//        }
    }
}
