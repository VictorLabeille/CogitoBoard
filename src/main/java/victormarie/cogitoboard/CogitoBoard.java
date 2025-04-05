package victormarie.cogitoboard;

import victormarie.cogitoboard.controleur.KanbanControleur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CogitoBoard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VueKanban.fxml"));
        Parent root = loader.load();

        KanbanControleur controleur = loader.getController();
        controleur.loadTaches();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("CogitoBoard - Votre Kanban intelligent");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        try {
            victormarie.cogitoboard.modele.dao.ConnexionBD.closeConnexion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}