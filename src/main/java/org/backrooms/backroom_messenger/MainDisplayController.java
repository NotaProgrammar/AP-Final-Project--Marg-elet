package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainDisplayController implements Initializable {
    private User user = null;
    private static List<Chat> searchedChatList = null;

    @FXML
    private ListView<Chat> chatListView;
    @FXML
    private TextField searchTextField;

    @FXML
    public void setUser(User user) {
        this.user = user;
    }


    public void toSettingPage(ActionEvent event) throws IOException {
        try {
            FXMLLoader settingLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("SettingPage.fxml"));
            Scene scene = new Scene(settingLoader.load(), 560, 350);
            SettingPageController spc = settingLoader.getController();
            spc.setUser(this.user);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatListView.setCellFactory(listView -> new ListCell<>() {
            private final Label nameLabel = new Label();
            private final Button openButton = new Button("Open");
            private final HBox content = new HBox(10, nameLabel, openButton);

            {
                // فاصله داخلی یا ظاهر دکمه و لیبل رو میشه اینجا تنظیم کرد
                content.setPadding(new Insets(5));
                openButton.setOnAction(event -> {
                    Chat selectedChat = getItem();
                    if (selectedChat != null) {
                        try {
                            goToChatPage(event, selectedChat);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Chat chat, boolean empty) {
                super.updateItem(chat, empty);
                if (empty || chat == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(chat.getName(user));
                    setGraphic(content);
                }
            }
        });

        // مقداردهی اولیه لیست
        if (user != null && user.getChats() != null) {
            chatListView.getItems().setAll(user.getChats());
        }
    }


    public void goToChatPage(ActionEvent event, Chat chat) throws IOException {
        FXMLLoader chatLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChatPage.fxml"));
        Scene scene = new Scene(chatLoader.load(), 900, 500);
        ChatPageController cpc = chatLoader.getController();
        cpc.setChatAndUser(chat, user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public static void searchResult(List<Chat> chats){
        searchedChatList.clear();
        searchedChatList.addAll(chats);
    }


    public void search(ActionEvent event)
    {
        String searchText = searchTextField.getText();
        //Client.search(searchText);
        while(searchedChatList.isEmpty()){}
        //todo : enter search page
    }
}
