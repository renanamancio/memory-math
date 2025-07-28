module jogodamemoria.memorymath {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;


    opens jogodamemoria.memorymath.controllers to javafx.fxml;

    opens jogodamemoria.memorymath to javafx.fxml;


    exports jogodamemoria.memorymath;
    exports jogodamemoria.memorymath.model;
}