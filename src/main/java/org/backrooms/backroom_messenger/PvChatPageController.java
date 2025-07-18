package org.backrooms.backroom_messenger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.backrooms.backroom_messenger.client.Client;
import org.backrooms.backroom_messenger.entity.*;

import java.io.IOException;
import java.util.Set;

public class PvChatPageController {

    private static PvChatPageController instance;
    private static User user = null;
    private static PvChat chat = null;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    private TextField Message;
    @FXML
    private ListView<Message> senderListView;
    @FXML
    private ListView<Message> receiverListView;

    public PvChatPageController() {
        instance = this;
    }


    @FXML
    public void initialize() {
        instance = this;
    }


    public void setChatAndUser(PvChat chat, User user) {
        PvChatPageController.chat = chat;
        PvChatPageController.user = user;
        messages.clear();
        messages.setAll(chat.getMessage());
        javafx.application.Platform.runLater(() -> {
            ScrollBar scrollBar1 = getVerticalScrollBar(senderListView);
            ScrollBar scrollBar2 = getVerticalScrollBar(receiverListView);

            if (scrollBar1 != null && scrollBar2 != null) {
                scrollBar1.valueProperty().bindBidirectional(scrollBar2.valueProperty());
            }
        });
    }


    public void setupCellFactories() {
        senderListView.setItems(messages);
        receiverListView.setItems(messages);

        senderListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else if (message.getSender().equals(user.getUsername())) {
                    setText(message.getMessage());
                } else {
                    setText(" ");
                }
            }
        });

        receiverListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else if (!message.getSender().equals(user.getUsername())) {
                    setText(message.getMessage());
                } else {
                    StringBuilder nextLine = new StringBuilder();
                    int c = 0;
                    for(int i=0; i<message.getMessage().length(); i++) {
                        if(message.getMessage().charAt(i) == '\n') {
                            nextLine.append("\n");
                        }
                    }
                    setText(nextLine.toString());
                }
            }
        });
    }


    public void sendMessage(ActionEvent event) {
        String message = Message.getText().trim();
        chat.getMessage().add(Client.sendMessage(message, chat));
        messages.setAll(chat.getMessage());
        Message.clear();
    }


    public static void saveReceivedMessage(Message message) {
        chat.getMessage().add(message);
        instance.messages.setAll(chat.getMessage());
    }


    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader displayLoader = new FXMLLoader(BackRoomMessengerApplication.class.getResource("MainDisplay.fxml"));
        Scene scene = new Scene(displayLoader.load(), 560, 350);
        MainDisplayController mdc  = displayLoader.getController();
        mdc.setUser(this.user);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public static PvChatPageController getInstance() {
        return instance;
    }

    public static User getUser() {
        return user;
    }

    public static PvChat getChat() {
        return chat;
    }

    private ScrollBar getVerticalScrollBar(ListView<?> listView) {
        Set<Node> nodes = listView.lookupAll(".scroll-bar");
        for (Node node : nodes) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                    return scrollBar;
                }
            }
        }
        return null;
    }
}
