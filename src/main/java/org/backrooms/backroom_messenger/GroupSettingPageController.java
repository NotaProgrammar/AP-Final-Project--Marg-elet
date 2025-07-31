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

public class GroupSettingPageController {
    private User user = null;
    private MultiUserChat group = null;
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
    private Label groupIdLabel;
    @FXML
    private Button groupIdButton;

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader groupPageLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("GroupChatPage.fxml"));
        Scene scene = new Scene(groupPageLoader.load(), 560, 350);
        GroupChatPageController ccpc  = groupPageLoader.getController();
        ccpc.setUserAndChat(user, group);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void changeName(ActionEvent event) throws IOException {
        String newName = newNameTextField.getText();
        if (newName.length() == 0) {
            newName = group.getName(user);
        }
        group.setName(newName);
        nameLabel.setText(newName);
        Client.changeName(group, newName);
    }


    @FXML
    public void changeDescription(ActionEvent event) throws IOException {
        String newDescription = newDescriptionTextField.getText();
        if (newDescription.length() == 0) {
            newDescription = group.getDescription();
        }
        group.setDescription(newDescription);
        descriptionLabel.setText(newDescription);
        Client.changeDescription(group, newDescription);
    }


    public void setUserAndGroup(User user, MultiUserChat group) {
        this.user = user;
        this.group = group;

        int count = group.getUsers().size();

        nameLabel.setText(group.getName(user));
        descriptionLabel.setText(group.getDescription());
        membersLabel.setText(Integer.toString(count));
        if(!group.getPublicity()) {
            idLabel.setText(String.valueOf(group.getId()));
            groupIdLabel.setDisable(false);
            groupIdLabel.setVisible(true);
            groupIdButton.setDisable(false);
            groupIdButton.setVisible(true);
        } else {
            idLabel.setDisable(true);
            idLabel.setVisible(false);
            groupIdLabel.setDisable(true);
            groupIdLabel.setVisible(false);
            groupIdButton.setDisable(true);
            groupIdButton.setVisible(false);
        }

        if(user.isSubed(group)) {
            String role = group.getRole(User.changeToPrivate(user));
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

        observableMembers = FXCollections.observableArrayList(group.getUsers());
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

                    if (!Objects.equals(group.getRole(member), "creator") && !member.equals(user)) {
                        Button kickButton = new Button("Kick");
                        kickButton.setOnAction(e -> {
                            Client.removeUser(member, group);
                            group.getUsers().remove(member);
                            //todo : remove from role list
                            observableMembers.remove(member);
                        });

                        Button roleButton = new Button();
                        boolean isAdmin = Objects.equals(group.getRole(member), "admin");
                        roleButton.setText(isAdmin ? "Ban the user" : "unban the user");
                        roleButton.setOnAction(e -> {
                            Client.changeUserRole(group, member);
                            group.changeRole(member);
                            roleButton.setText(Objects.equals(group.getRole(member), "Ban the user") ? "v" : "unban the user");
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
        groupIdLabel.setDisable(true);
        groupIdLabel.setVisible(false);
        groupIdButton.setDisable(true);
        groupIdButton.setVisible(false);
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
