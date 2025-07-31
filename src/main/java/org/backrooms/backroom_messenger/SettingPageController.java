package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class SettingPageController {
    private User user = null;

    @FXML
    private DatePicker datePicker;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameField;
    @FXML
    private Label nameNotifLabel;
    @FXML
    private PasswordField lastPasswordField;
    @FXML
    private Button changePasswordButton;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label warning;
    @FXML
    private Button checkPasswordButton;
    @FXML
    private TextField bioField;
    @FXML
    private Label bioLabel;



    @FXML
    public void setUser(User user) {
        this.user = user;
        userNameLabel.setText(user.getUsername());
        bioLabel.setText(user.getBio());
        nameLabel.setText(user.getName());
        try {
            showPasswordTextField(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Client.signOut();
        toMainPage(event);
    }


    public void toMainPage(ActionEvent event) throws IOException {
        FXMLLoader mainPageLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainPage.fxml"));
        Scene scene = new Scene(mainPageLoader.load(), 560, 350);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void changeName(ActionEvent event) throws Exception {
        String newName = nameField.getText();
        if(newName.isEmpty()) {
            newName = user.getName();
        }
        user.setName(newName);
        Client.changeUserProperty("name",newName);
        nameLabel.setText(newName);
        nameNotifLabel.setTextFill(Color.GREEN);
        nameNotifLabel.setText("you name successfully changed!");
    }



    private void showPasswordTextField(boolean bool) throws IOException {
        newPasswordField.setVisible(bool);
        newPasswordField.setDisable(!bool);
        changePasswordButton.setVisible(bool);
        changePasswordButton.setDisable(!bool);
        checkPasswordButton.setDisable(bool);
        checkPasswordButton.setVisible(!bool);
        lastPasswordField.setDisable(bool);
        lastPasswordField.setVisible(!bool);
    }

    @FXML
    public void changePassword(ActionEvent event) throws Exception {
        Client.changeUserProperty("password",newPasswordField.getText());
        showPasswordTextField(false);
    }


    @FXML
    public void checkPassword(ActionEvent event) throws IOException {
        if(user.checkPassword(lastPasswordField.getText())) {
            showPasswordTextField(true);
            warning.setTextFill(Color.BLACK);
            warning.setText("Enter new Password: ");
        }else{
            warning.setTextFill(Color.RED);
            warning.setText("Wrong password!");
        }
    }

    @FXML
    public void changeBio(ActionEvent event) throws Exception {
        Client.changeUserProperty("bio",bioField.getText());
        bioLabel.setText(bioField.getText());
    }
}
