package org.backrooms.backroom_messenger;

import javafx.fxml.FXML;
import org.backrooms.backroom_messenger.entity.User;

public class SettingPageController {
    private User user = null;

    @FXML
    public void setUser(User user) {
        this.user = user;
    }
}
