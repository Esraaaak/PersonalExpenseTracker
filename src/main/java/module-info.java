module com.mycompany.personalexpensetrackerr {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.personalexpensetrackerr to javafx.fxml;
    opens controller to javafx.fxml;
    opens model to javafx.fxml;

    exports com.mycompany.personalexpensetrackerr;
}
