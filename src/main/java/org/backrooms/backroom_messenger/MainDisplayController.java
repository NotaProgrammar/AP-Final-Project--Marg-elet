package org.backrooms.backroom_messenger;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

public class MainDisplayController implements Initializable {
    private User user = null;
    private static List<Chat> searchedChatList = new ArrayList<>();
    private static Chat chosenChat = null;
    private static boolean chatFound = false;



    @FXML
    private Button searchButton;
    @FXML
    private Button newChanelButton;
    @FXML
    private Button newGroupButton;
    @FXML
    private Button settingButton;
    @FXML
    private ListView<Chat> chatListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label searchResult;

    @FXML
    public void initialize() {
        Font.loadFont(getClass().getResource("fonts/MaidenOrange.ttf").toExternalForm(), 18);
        Font.loadFont(getClass().getResource("/fonts/MaShanZheng.ttf").toExternalForm(), 18);

        searchButton.setFont(Font.font("Maiden Orange Regular", 18));
        newChanelButton.setFont(Font.font("Ma Shan Zheng Regular", 20));
        newGroupButton.setFont(Font.font("Ma Shan Zheng Regular", 18));
        settingButton.setFont(Font.font("Ma Shan Zheng Regular", 18));

    }
    @FXML
    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getChats() != null) {
            chatListView.getItems().clear();
            chatListView.getItems().setAll(user.getChats());
        }
        chosenChat = null;
    }


    public void toSettingPage(ActionEvent event) throws IOException {
        try {
            FXMLLoader settingLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("SettingPage.fxml"));
            Scene scene = new Scene(settingLoader.load(), 654, 579);
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
            private final ImageView profile = new ImageView();
            private final Label nameLabel = new Label();
            private final Button openButton = new Button("Open");
            private final HBox content = new HBox(10,profile, nameLabel, openButton);

            {
                content.setPadding(new Insets(5));
                openButton.setOnAction(event -> {
                    chosenChat = null;
                    Chat selectedChat = getItem();
                    if (selectedChat != null) {
                        try {
                            if(selectedChat instanceof PvChat pv){
                                goToPvChatPage(event, pv);
                            }
                            if(selectedChat instanceof MultiUserChat muc)
                            {
                                if(muc.isChannel()){
                                    goToChannelPage(event, selectedChat);
                                }else{
                                    goToGroupPage(event, selectedChat);
                                }
                            }

                        } catch (IOException | InterruptedException e) {
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
                    if(chat.getProfile(user) != null){
                        byte[] imageBytes = Base64.getDecoder().decode(chat.getProfile(user));
                        profile.setImage(new Image(new ByteArrayInputStream(imageBytes)));
                        profile.setFitHeight(25);
                        profile.setFitWidth(25);
                    }
                    setGraphic(content);
                }
            }
        });
    }


    public void goToChannelPage(ActionEvent event, Chat chat) throws IOException, InterruptedException {
        Client.openChat(chat, 1);
        while(chosenChat == null){
            Thread.sleep(100);
        }
        try{
            FXMLLoader channelLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChannelChatPage.fxml"));
            Scene scene = new Scene(channelLoader.load(), 900, 550);
            ChannelChatPageController ccpc = channelLoader.getController();
            ccpc.setUserAndChat(user, (MultiUserChat) chosenChat);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e);
        }

    }


    public void goToGroupPage(ActionEvent event, Chat chat) throws InterruptedException, IOException {
        Client.openChat(chat, 1);
        while(chosenChat == null){
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


    public void goToPvChatPage(ActionEvent event, PvChat chat) throws IOException, InterruptedException {
        Client.openChat(chat, 1);
        PrivateUser user1 = chat.getUser1();
        PrivateUser user2 = chat.getUser2();
        while(chosenChat == null){
            Thread.sleep(100);
        }
        PvChat pv = (PvChat) chosenChat;
        pv.setUser(user1);
        pv.setUser(user2);
        FXMLLoader pvChatLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("PvChatPage.fxml"));
        Scene scene = new Scene(pvChatLoader.load(), 900, 550);
        PvChatPageController cpc = pvChatLoader.getController();
        cpc.setChatAndUser(pv, user);
        cpc.setupCellFactories();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public static void pvChatResult(Chat selectedChat) {
        chosenChat = selectedChat;
    }


    public static void searchResult(List<Chat> chats){
        searchedChatList.clear();
        searchedChatList.addAll(chats);
        chatFound = true;
    }


    public void search(ActionEvent event) throws Exception {
        chatFound = false;
        String searchText = searchTextField.getText();
        Client.search(searchText);
        while(!chatFound){
            Thread.sleep(100);
        }
        if(searchedChatList.isEmpty()){
            searchResult.setTextFill(Color.RED);
            searchResult.setText("Nothing Found");
        }else{
            FXMLLoader searchLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("SearchPage.fxml"));
            try{
                Scene scene = new Scene(searchLoader.load(), 900, 550);
                SearchPageController spc = searchLoader.getController();
                spc.setChatList(searchedChatList, user);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }


    public void goToCreatChannel(ActionEvent event) throws IOException {
        FXMLLoader creatChannelLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("CreatChannelPage.fxml"));
        Scene scene = new Scene(creatChannelLoader.load(), 900, 550);
        CreateChannelPageController ccpc = creatChannelLoader.getController();
        ccpc.setUser(user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void goToCreateGroup(ActionEvent event) throws IOException{
        FXMLLoader createGroupLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("CreatGroupPage.fxml"));
        Scene scene = new Scene(createGroupLoader.load(), 900, 550);
        CreateGroupPageController cgpc = createGroupLoader.getController();
        cgpc.setUser(user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
