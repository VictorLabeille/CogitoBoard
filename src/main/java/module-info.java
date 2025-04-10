module victormarie.cogitoboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires mysql.connector.j;
    requires tensorflow;

    opens victormarie.cogitoboard to javafx.fxml;
    opens victormarie.cogitoboard.controleur to javafx.fxml;
    opens victormarie.cogitoboard.modele to javafx.fxml;
    opens victormarie.cogitoboard.modele.dao to javafx.fxml;

    exports victormarie.cogitoboard;
}
