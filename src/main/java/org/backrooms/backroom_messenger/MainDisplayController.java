package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.entity.Chat;
import org.backrooms.backroom_messenger.entity.User;

import java.io.IOException;

public class MainDisplayController {
    private User user = null;

    @FXML
    private ListView<Chat> chatListView;

    @FXML
    public void setUser(User user) {
        this.user = user;
    }

    public void toSettingPage(ActionEvent event) throws IOException {
        try {
            FXMLLoader settingLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("SettingPage.fxml"));
            Scene scene = new Scene(settingLoader.load(), 560, 350);
            SettingPageController spc = settingLoader.getController();
            spc.setUser(this.user);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void initialize() {

    }
}
