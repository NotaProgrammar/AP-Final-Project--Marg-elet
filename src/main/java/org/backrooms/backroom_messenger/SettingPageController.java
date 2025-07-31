package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Label lastPasswordLabel;
    @FXML
    private TextField lastPasswordField;
    @FXML
    private Button checkPasswordButton;


    @FXML
    public void setUser(User user) {
        this.user = user;
        userNameLabel.setText(user.getUsername());
        nameLabel.setText(user.getName());
        lastPasswordLabel.setDisable(true);
        lastPasswordField.setDisable(true);
        checkPasswordButton.setDisable(true);

    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Client.signOut();
        toMainPage(event);
    }


    public void toMainPage(ActionEvent event) throws IOException {
        FXMLLoader mainPageLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainPage.fxml"));
        Scene scene = new Scene(mainPageLoader.load(), 560, 350);
        MainPageController mpc = mainPageLoader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void deleteAccount(ActionEvent event) throws IOException {
        //todo : probably calls dome function from client
        toMainPage(event);
    }


    @FXML
    public void changeName(ActionEvent event) throws IOException {
        String newName = nameField.getText();
        if(newName.isEmpty()) {
            newName = user.getName();
        }
        nameLabel.setText(newName);
        nameNotifLabel.setTextFill(Color.GREEN);
        nameNotifLabel.setText("you name successfully changed!");
    }


    @FXML
    public void showPasswordTextField(ActionEvent event) throws IOException {
        lastPasswordLabel.setVisible(true);
        lastPasswordField.setVisible(true);
        checkPasswordButton.setVisible(true);
    }


    @FXML
    public void checkPassword(ActionEvent event) throws IOException {
        //todo : calls a function to check password
    }
}
