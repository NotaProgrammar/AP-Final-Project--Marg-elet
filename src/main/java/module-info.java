module org.backrooms.backroom_messager {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.backrooms.backroom_messenger to javafx.fxml;
    exports org.backrooms.backroom_messenger;
}