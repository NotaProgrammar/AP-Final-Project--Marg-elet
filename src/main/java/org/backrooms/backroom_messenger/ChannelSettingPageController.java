package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.MultiUserChat;
import org.backrooms.backroom_messenger.entity.PrivateUser;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.util.Objects;

public class ChannelSettingPageController {

    private User user = null;
    private MultiUserChat channel = null;
    ObservableList<PrivateUser> observableMembers;

    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label idLabel;
    @FXML
    private TextField newNameTextField;
    @FXML
    private TextField newDescriptionTextField;
    @FXML
    private Label changeNameLabel;
    @FXML
    private Button changeNameButton;
    @FXML
    private Label changeDescriptionLabel;
    @FXML
    private Button changeDescriptionButton;
    @FXML
    private ListView<PrivateUser> membersListView;
    @FXML
    private Label membersLabel;
    @FXML
    private Label channelIdLabel;
    @FXML
    private Button channelIdButton;

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader channelPageLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("ChannelChatPage.fxml"));
        Scene scene = new Scene(channelPageLoader.load(), 560, 350);
        ChannelChatPageController ccpc  = channelPageLoader.getController();
        ccpc.setUserAndChat(user, channel);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void changeName(ActionEvent event) throws IOException {
        String newName = newNameTextField.getText();
        if (newName.length() == 0) {
            newName = channel.getName(user);
        }
        channel.setName(newName);
        nameLabel.setText(newName);
        Client.changeName(channel, newName);
    }


    @FXML
    public void changeDescription(ActionEvent event) throws IOException {
        String newDescription = newDescriptionTextField.getText();
        if (newDescription.length() == 0) {
            newDescription = channel.getDescription();
        }
        channel.setDescription(newDescription);
        descriptionLabel.setText(newDescription);
        Client.changeDescription(channel, newDescription);
    }


    public void setUserAndChannel(User user, MultiUserChat channel) {
        this.user = user;
        this.channel = channel;

        int count = channel.getUsers().size();

        nameLabel.setText(channel.getName(user));
        descriptionLabel.setText(channel.getDescription());
        membersLabel.setText(Integer.toString(count));
        if(!channel.getPublicity()) {
            idLabel.setText(String.valueOf(channel.getId()));
            channelIdLabel.setDisable(false);
            channelIdLabel.setVisible(true);
            channelIdButton.setDisable(false);
            channelIdButton.setVisible(true);
        } else {
            idLabel.setDisable(true);
            idLabel.setVisible(false);
            channelIdLabel.setDisable(true);
            channelIdLabel.setVisible(false);
            channelIdButton.setDisable(true);
            channelIdButton.setVisible(false);
        }

        if(user.isSubed(channel)) {
            String role = channel.getRole(User.changeToPrivate(user));
            switch(role){
                case "creator" :
                    break;
                case "admin" :
                    changeNameLabel.setDisable(true);
                    changeNameLabel.setVisible(false);
                    changeNameButton.setDisable(true);
                    changeNameButton.setVisible(false);
                    changeDescriptionLabel.setDisable(true);
                    changeDescriptionLabel.setVisible(false);
                    changeDescriptionButton.setDisable(true);
                    changeDescriptionButton.setVisible(false);
                    newNameTextField.setDisable(true);
                    newNameTextField.setVisible(false);
                    newDescriptionTextField.setDisable(true);
                    newDescriptionTextField.setVisible(false);
                    break;
                case "normal" :
                    hideAll();
                    break;
            }
        }else {
            hideAll();
        }

        observableMembers = FXCollections.observableArrayList(channel.getUsers());
        membersListView.setItems(observableMembers);

        membersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PrivateUser member, boolean empty) {
                super.updateItem(member, empty);
                if (empty || member == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label nameLabel = new Label(member.getName());
                    Label usernameLabel = new Label("@" + member.getUsername());
                    HBox.setHgrow(nameLabel, Priority.ALWAYS);
                    HBox.setHgrow(usernameLabel, Priority.ALWAYS);
                    HBox hBox = new HBox(10, nameLabel, usernameLabel);

                    if (!Objects.equals(channel.getRole(member), "creator") && !member.equals(user)) {
                        Button kickButton = new Button("Kick");
                        kickButton.setOnAction(e -> {
                            Client.removeUser(member, channel);
                            channel.getUsers().remove(member);
                            //todo : remove from role list
                            observableMembers.remove(member);
                        });

                        Button roleButton = new Button();
                        boolean isAdmin = Objects.equals(channel.getRole(member), "admin");
                        roleButton.setText(isAdmin ? "Change to Normal" : "Change to Admin");
                        roleButton.setOnAction(e -> {
                            Client.changeUserRole(channel, member);
                            channel.changeRole(member);
                            roleButton.setText(Objects.equals(channel.getRole(member), "admin") ? "Change to Normal" : "Change to Admin");
                        });

                        hBox.getChildren().addAll(kickButton, roleButton);
                    }
                    setGraphic(hBox);
                }
            }
        });
    }


    public void hideAll() {
        membersListView.setDisable(true);
        membersListView.setVisible(false);
        changeNameLabel.setDisable(true);
        changeNameLabel.setVisible(false);
        changeNameButton.setDisable(true);
        changeNameButton.setVisible(false);
        changeDescriptionLabel.setDisable(true);
        changeDescriptionLabel.setVisible(false);
        changeDescriptionButton.setDisable(true);
        changeDescriptionButton.setVisible(false);
        newNameTextField.setDisable(true);
        newNameTextField.setVisible(false);
        newDescriptionTextField.setDisable(true);
        newDescriptionTextField.setVisible(false);
        idLabel.setDisable(true);
        idLabel.setVisible(false);
        channelIdLabel.setDisable(true);
        channelIdLabel.setVisible(false);
        channelIdButton.setDisable(true);
        channelIdButton.setVisible(false);
    }


    @FXML
    public void copyId(ActionEvent event) throws IOException {
        String id = idLabel.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(id);
        clipboard.setContent(content);
    }

}
