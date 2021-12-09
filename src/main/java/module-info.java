module org.manuel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;
    requires mysql.connector.java;

    opens org.manuel to javafx.fxml;
    opens org.manuel.controllers to javafx.fxml;
    opens org.manuel.models to javafx.base;
    opens org.manuel.uicontrols to javafx.fxml;
    exports org.manuel;
}