package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.sql.SQLException;

public class MainPageController {

    @FXML
    private TextField Username;
    @FXML
    private PasswordField Password;
    @FXML
    private Button Enter;
    @FXML
    private Button Login;
    @FXML
    private Label ErrorMessage;

    @FXML
    public void SignIn(ActionEvent event) throws IOException, SQLException {
        String Username = this.Username.getText();
        String Password = this.Password.getText();
    }

    public void Enter(ActionEvent event) throws IOException, SQLException {
        try{
            String Username = this.Username.getText();
            String Password = this.Password.getText();

            if ((Username == null || Username.isEmpty()) || (Password == null || Password.isEmpty())) {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("Please Fill All Fields");
            } else {
                User selectedUser = null;
                //todo : make a fanction wich it search usernames and if it is token, something like this :
                // todo : selectedUser = DatabaseManager.Select_User(username);
                if (selectedUser != null) {
                    ErrorMessage.setTextFill(Color.RED);
                    ErrorMessage.setText("username already taken");
                } else {
                    //todo : make a new user and add it to data base
                    toMainDisplay(event, selectedUser);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void toLoginPage(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loginLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(loginLoader.load(), 560, 350);
        LoginPageController lpc = loginLoader.getController();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void toMainDisplay(ActionEvent event, User user) throws IOException {
        FXMLLoader displayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(displayLoader.load(), 560, 400);
        MainDisplayController mdc = displayLoader.getController();
        mdc.setUser(user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
