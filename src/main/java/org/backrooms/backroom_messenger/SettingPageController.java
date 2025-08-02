package org.backrooms.backroom_messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingPageController {
    private User user = null;

    @FXML
    private Label userNameLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameField;
    @FXML
    private Label nameNotifLabel;
    @FXML
    private PasswordField lastPasswordField;
    @FXML
    private Button changePasswordButton;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label warning;
    @FXML
    private Button checkPasswordButton;
    @FXML
    private TextField bioField;
    @FXML
    private Label bioLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private Label newPasslabel;
    @FXML
    private Label lastPassLabel;



    @FXML
    public void setUser(User user) {
        this.user = user;
        userNameLabel.setText(user.getUsername());
        bioLabel.setText(user.getBio());
        nameLabel.setText(user.getName());
        String imageBase64 = user.getImageBase64();
        if(imageBase64 != null) {
            byte[] bytes = Base64.getDecoder().decode(imageBase64);
            imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
        }
        try {
            showPasswordTextField(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void setImage(ActionEvent event) {
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.gif", "*.bmp")
            );
            File file = fileChooser.showOpenDialog(null);
            byte[] image = Files.readAllBytes(file.toPath());
            Client.setImageForUsers(image);
            if (file != null) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }catch(Exception ignored){

        }
    }

    @FXML
    public void deleteImage(ActionEvent event){
        Client.setImageForUsers(null);
        imageView.setImage(null);
    }

    @FXML
    public void logOut(ActionEvent event) throws IOException {
        Client.signOut();
        user = null;
        toMainPage(event);
    }


    public void toMainPage(ActionEvent event) throws IOException {
        FXMLLoader mainPageLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainPage.fxml"));
        Scene scene = new Scene(mainPageLoader.load(), 560, 350);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void changeName(ActionEvent event) throws Exception {
        String newName = nameField.getText();
        if(newName.isEmpty()) {
            newName = user.getName();
        }
        user.setName(newName);
        Client.changeUserProperty("name",newName);
        nameLabel.setText(newName);
        nameNotifLabel.setTextFill(Color.GREEN);
        nameNotifLabel.setText("you name successfully changed!");
    }



    private void showPasswordTextField(boolean bool) throws IOException {
        newPasswordField.setVisible(bool);
        newPasswordField.setDisable(!bool);
        newPasslabel.setVisible(bool);
        changePasswordButton.setVisible(bool);
        changePasswordButton.setDisable(!bool);
        checkPasswordButton.setDisable(bool);
        checkPasswordButton.setVisible(!bool);
        lastPasswordField.setDisable(bool);
        lastPasswordField.setVisible(!bool);
        lastPassLabel.setVisible(!bool);
    }

    @FXML
    public void changePassword(ActionEvent event) throws Exception {
        String password = newPasswordField.getText();
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            warning.setTextFill(Color.RED);
            warning.setText("Password must contain uppercase, lowercase, number, and symbol (min 8 chars)");
            return;
        }
        warning.setTextFill(Color.GREEN);
        warning.setText("Your password has been changed!");
        lastPasswordField.clear();
        newPasswordField.clear();
        Client.changeUserProperty("password",password);
        showPasswordTextField(false);
    }


    @FXML
    public void checkPassword(ActionEvent event) throws IOException {
        if(user.checkPassword(lastPasswordField.getText())) {
            showPasswordTextField(true);
        }else{
            warning.setTextFill(Color.RED);
            warning.setText("Wrong password!");
        }
    }

    @FXML
    public void changeBio(ActionEvent event) throws Exception {
        Client.changeUserProperty("bio",bioField.getText());
        bioLabel.setText(bioField.getText());
    }
    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader mainDisplayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(mainDisplayLoader.load(), 560, 350);
        MainDisplayController mdc = mainDisplayLoader.getController();
        mdc.setUser(user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
