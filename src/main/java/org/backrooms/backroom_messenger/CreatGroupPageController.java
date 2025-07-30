package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class CreatGroupPageController {

    private User user = null;
    private MultiUserChat group = null;
    boolean publicGroup = false;

    @FXML
    private TextField description;
    @FXML
    private TextField name;
    @FXML
    private CheckBox publicity;
    @FXML
    private Label message;


    public void create(ActionEvent event) throws IOException {
        String groupName = name.getText();
        String groupDescription = description.getText();
//        if(description.getText().isEmpty() || name.getText().isEmpty()){
//            message.setTextFill(Color.RED);
//            message.setText("Please Fill All Fields");
//        } else {
//            group = Client.creatGroup(groupName, groupDescription, publicGroup);
//            group.getUsers().add(User.changeToPrivate(user));
//            group.getRoles().add("creator");
//            user.getChats().add(group);
//            goToGroupPage(event);
//        }

    }


    public void setPublicityOfGroup(ActionEvent event){
        if(publicity.isSelected()){
            publicGroup = true;
        }
    }


    public void goToGroupPage(ActionEvent event) throws IOException {
        FXMLLoader groupLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("GroupChatPage.fxml"));
        Scene scene = new Scene(groupLoader.load(), 900, 550);
        GroupChatPageController gcpc = groupLoader.getController();
        gcpc.setUserAndChat(user, group);
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


}
