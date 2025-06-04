module com.example.compilador {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;


    opens com.example.compilador to javafx.fxml;
    exports com.example.compilador;
    exports com.example.compilador.controller;
    opens com.example.compilador.controller to javafx.fxml;
    exports com.example.compilador.models;
    opens com.example.compilador.models to javafx.fxml;
    exports com.example.compilador.functions;
    opens com.example.compilador.functions to javafx.fxml;
}