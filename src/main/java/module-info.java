module steurer.software_two {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens steurer.software_two.view to javafx.fxml;
    opens steurer.software_two.controller to javafx.fxml;
    opens steurer.software_two.model to javafx.fxml;
    exports steurer.software_two;
    exports steurer.software_two.controller;
    exports steurer.software_two.model;

}