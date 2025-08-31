package org.backrooms.backroom_messenger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainPageController {

    @FXML
    private AnchorPane signupPane;
    @FXML
    private TextField Username;
    @FXML
    private PasswordField Password;
    @FXML
    private Label ErrorMessage;


    public void Enter(ActionEvent event) throws IOException, SQLException {
        try{
            String Username = this.Username.getText();
            String Password = this.Password.getText();
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
            Pattern pattern = Pattern.compile(passwordRegex);
            Matcher matcher = pattern.matcher(Password);

            if (!matcher.matches()) {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("Password must contain uppercase, lowercase, number, and symbol (min 8 chars)");
                return;
            }

            String usernameRegex = "[\\-_=!@#$%^&*()/?.>;:'<,]";
            pattern = Pattern.compile(usernameRegex);
            matcher = pattern.matcher(Username);

            if (matcher.find()) {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("Username must not contain special characters like - or @");
                return;
            }

            if ((Username == null || Username.isEmpty()) || (Password == null || Password.isEmpty())) {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("Please Fill All Fields");
            } else {
                User selectedUser = null;
                selectedUser = Client.signup(Username, Password);
                if (selectedUser == null) {
                    ErrorMessage.setTextFill(Color.RED);
                    ErrorMessage.setText("username already taken");
                } else {
                    toMainDisplay(event, selectedUser);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void toLoginPage(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loginLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(loginLoader.load(), 600, 400);
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
