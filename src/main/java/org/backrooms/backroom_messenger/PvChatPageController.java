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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class PvChatPageController {

    private static PvChatPageController instance;
    private static User user = null;
    private static PvChat pv = null;
    private  ObservableList<Message> messages = FXCollections.observableArrayList();
    private static MultiUserChat opened = null;
    private static boolean isChannelOpened = false;
    private static Lock listViewLock = new ReentrantLock();

    @FXML
    private TextField Message;
    @FXML
    private ListView<Message> MessageListView;



    public void setChatAndUser(PvChat chat, User user) {
        pv = chat;
        PvChatPageController.user = user;
        pv.getMessage().sort(Comparator.comparing(org.backrooms.backroom_messenger.entity.Message::getDate));
        messages.clear();
        messages.setAll(pv.getMessage());
    }

    public PvChatPageController() {
        instance = this;
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
                    Label messageLabel = new Label(message.toString(user.getUsername()));

                    Button chatButton = new Button("Open Chat");
                    chatButton.setOnAction(e -> {
                        openChat(e,message.getLinkToMultiUserChat());
                    });
                    Button downloadbutton = new Button("Download File");
                    downloadbutton.setOnAction(e -> {
                        downloadFile(message);
                    });

                    // Layout for the cell content
                    HBox cellBox = new HBox(10);
                    cellBox.setAlignment(message.getSender().equals(user.getUsername())
                            ? Pos.CENTER_RIGHT
                            : Pos.CENTER_LEFT);

                    if (message.getLinkToMultiUserChat() != null) {
                        messageLabel.setText(message.getLinkToMultiUserChat().getName(null));
                        cellBox.getChildren().addAll(messageLabel, chatButton);
                    } else if (message.isFileExists()){
                        cellBox.getChildren().addAll(messageLabel,downloadbutton);
                    } else{
                        cellBox.getChildren().add(messageLabel);
                    }

                    setText(null);
                    setGraphic(cellBox);
                }
            }
        });

    }

    private void downloadFile(Message message) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.dir")));
        dc.setTitle("Select Directory");
        File directory = dc.showDialog(null);
        if (directory != null) {
            Client.downloadFile(message,directory.getAbsolutePath());
        }
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
        Client.openChat(chat, 3);
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
        Client.openChat(selected, 3);
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


    public void sendMessage(ActionEvent event) {
        String messageString = Message.getText().trim();
        Message messge = Client.sendMessage(messageString, pv,false);
        saveReceivedMessage(messge);
        Message.clear();
    }


    public static void saveReceivedMessage(Message message) {
        try{
            listViewLock.lock();
            pv.getMessage().add(message);
            instance.messages.clear();
            instance.messages.addAll(pv.getMessage());
            listViewLock.unlock();
        }catch (Exception e){
            System.out.println(e);
        }
    }


    public static void refresh(){
        listViewLock.lock();
        instance.messages.clear();
        instance.messages.addAll(pv.getMessage());
        listViewLock.unlock();
    }


    public void goBack(ActionEvent event) throws IOException {
        pv = null;
        FXMLLoader displayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(displayLoader.load(), 560, 350);
        MainDisplayController mdc  = displayLoader.getController();
        mdc.setUser(this.user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void toProfile(ActionEvent event) throws IOException {
        FXMLLoader profileLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ProfilePage.fxml"));
        Scene scene = new Scene(profileLoader.load(), 560, 350);
        ProfilePageController ppc= profileLoader.getController();
        ppc.setUserAndChat(user,pv.getUser(user), pv);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public static User getUser() {
        return user;
    }


    public static PvChat getChat() {
        return pv;
    }

    public void openFile(ActionEvent event){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files", "*")
            );
            File file = fileChooser.showOpenDialog(null);
            if(file != null){
                byte[] bytes = Files.readAllBytes(file.toPath());
                saveReceivedMessage(Client.sendFile(file.getName(),bytes,pv,true));
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }

}
