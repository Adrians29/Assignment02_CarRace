module com.adrianegl {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.adrianegl to javafx.fxml;
    exports com.adrianegl;
}
