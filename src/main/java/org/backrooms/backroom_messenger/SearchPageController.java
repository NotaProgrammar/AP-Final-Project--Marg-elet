package org.backrooms.backroom_messenger;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.backrooms.backroom_messenger.entity.Chat;

import java.util.List;

public class SearchPageController {

    private List<Chat> chatList;

    @FXML
    private ListView<Chat> chatListView;

    public void setChatList(List<Chat> chatList) {
        this.chatList.addAll(chatList);
    }

}
