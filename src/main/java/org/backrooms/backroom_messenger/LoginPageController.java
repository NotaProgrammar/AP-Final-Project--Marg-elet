package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class LoginPageController {

    @FXML
    private TextField Username;
    @FXML
    private TextField Password;
    @FXML
    private Label ErrorMessage;

    @FXML
    public void Enter(ActionEvent event) throws IOException {
        String Username = this.Username.getText();
        String Password = this.Password.getText();

        if((Username == null || Username.isEmpty()) || (Password == null || Password.isEmpty())) {
            ErrorMessage.setTextFill(Color.RED);
            ErrorMessage.setText("Please Fill All Fields");
        }

        else {
            User selectedUser = null;
            //todo : search in data base something like this :
            // todo : selectedUser = DatabaseManager.Select_User(username);
            if(selectedUser == null) {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("username not found");
            }
            else if(Password.equals(selectedUser.getPassword()))
            {
                toMainDisplay(event, selectedUser);
            }
            else
            {
                ErrorMessage.setTextFill(Color.RED);
                ErrorMessage.setText("password does not match");
            }
        }
    }

    public void toMainDisplay(ActionEvent event, User user) throws IOException {
        FXMLLoader displayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(displayLoader.load(), 900, 500);
        MainDisplayController mdc = displayLoader.getController();
        mdc.setUser(user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
