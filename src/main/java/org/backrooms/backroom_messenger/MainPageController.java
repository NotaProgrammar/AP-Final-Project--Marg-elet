package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
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
    public void Enter(ActionEvent event) throws IOException, SQLException {
        String Username = this.Username.getText();
        String Password = this.Password.getText();

        if((Username == null || Username.isEmpty()) || (Password == null || Password.isEmpty())) {
            ErrorMessage.setTextFill(Color.RED);
            ErrorMessage.setText("Please Fill All Fields");
        }

        else {
            User selectedUser = null;
            //todo : make a fanction wich it search usernames and if it is token, something like this :
            // todo : selectedUser = DatabaseManager.Select_User(username);
            if(selectedUser != null) {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("username already taken");
            }

            else {
                //todo : make a new user and add it to data base

            }
        }
    }



}
