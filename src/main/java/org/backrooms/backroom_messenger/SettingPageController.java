package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.User;

public class SettingPageController {
    private User user = null;

    @FXML
    private DatePicker datePicker;

    @FXML
    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void logOut(ActionEvent event) {
        Client.signOut();

    }
}
