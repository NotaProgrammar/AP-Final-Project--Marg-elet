package org.backrooms.backroom_messenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;

public class BackRoomMessengerApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(BackRoomMessengerApplication.class.getResource("LoginPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 560, 400);
            stage.setTitle("Margelet");
            stage.setOnCloseRequest(event -> {
                if(Client.getUser() != null) {
                    Client.signOut();
                    System.exit(0);
                }
            });
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}