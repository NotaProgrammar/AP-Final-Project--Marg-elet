package org.backrooms.backroom_messenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class BackRoomMessengerApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(BackRoomMessengerApplication.class.getResource("MainPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setTitle("Margelet");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(this.getClass());
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}