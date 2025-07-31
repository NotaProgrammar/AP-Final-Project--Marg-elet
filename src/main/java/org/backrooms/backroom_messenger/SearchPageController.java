package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchPageController implements Initializable {

    private List<Chat> chatList = new ArrayList<>();
    private User user;
    private static Chat chosenChat;
    private static boolean chatFound;

    @FXML
    private ListView<Chat> chatListView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chatListView.setCellFactory(listView -> new ListCell<>() {
            private final HBox content;
            private final Label nameLabel;
            private final Button openButton;

            {
                nameLabel = new Label();
                openButton = new Button("Open Chat");
                openButton.setOnAction(event -> {
                    chatFound = false;
                    chosenChat = null;
                    Chat chat = getItem(); // گرفتن آیتم مربوط به این سلول
                    if (chat != null) {
                        try {
                            if (chat instanceof PvChat) {
                                goToPvChatPage(event, chat);
                            }
                            if(chat instanceof MultiUserChat muc)
                            {
                                if(muc.isChannel()){
                                    goToChannelPage(event, chat);
                                }else{
                                    goToGroupPage(event, chat);
                                }
                            }
                        }catch (Exception e){
                            System.out.println(e);
                        }
                    }
                });

                content = new HBox(10, nameLabel, openButton);
                content.setPadding(new Insets(5));
                nameLabel.setPrefWidth(200);
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
    }


    public void goToPvChatPage(ActionEvent event, Chat chat) throws IOException, InterruptedException {
        Client.openChat(chat, 2);
        while(!chatFound){
            Thread.sleep(100);
        }
        FXMLLoader pvChatLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("PvChatPage.fxml"));
        Scene scene = new Scene(pvChatLoader.load(), 900, 550);
        PvChatPageController cpc = pvChatLoader.getController();
        cpc.setChatAndUser((PvChat) chosenChat, user);
        cpc.setupCellFactories();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void goToChannelPage(ActionEvent event, Chat chat) throws IOException, InterruptedException {
        Client.openChat(chat, 2);
        while(!chatFound){
            Thread.sleep(100);
        }
        FXMLLoader channelLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChannelChatPage.fxml"));
        Scene scene = new Scene(channelLoader.load(), 900, 550);
        ChannelChatPageController ccpc = channelLoader.getController();
        ccpc.setUserAndChat(user, (MultiUserChat) chosenChat);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void goToGroupPage(ActionEvent event, Chat chat) throws InterruptedException, IOException {
        Client.openChat(chat, 2);
        while(!chatFound){
            Thread.sleep(100);
        }
        FXMLLoader groupLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("GroupChatPage.fxml"));
        Scene scene = new Scene(groupLoader.load(), 900, 550);
        GroupChatPageController gcpc = groupLoader.getController();
        gcpc.setUserAndChat(user, (MultiUserChat) chosenChat);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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


    public void setChatList(List<Chat> chatList, User user) {
        this.chatList.addAll(chatList);
        this.user = user;
        chatListView.getItems().clear();
        chatListView.getItems().setAll(chatList);
    }


    public static void openChatInSearchResult(Chat selectedChat) {
        chatFound = true;
        chosenChat = selectedChat;
    }


}
