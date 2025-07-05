package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    public void SignIn(ActionEvent event) throws IOException, SQLException {
        String Username = this.Username.getText();
        String Password = this.Password.getText();
    }

}
