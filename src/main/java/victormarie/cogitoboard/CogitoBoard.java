package victormarie.cogitoboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import victormarie.cogitoboard.controleur.ControleurConnexionBD;
import victormarie.cogitoboard.modele.dao.ConfigurationBD;

public class CogitoBoard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Afficher la fenêtre de connexion à la BD d'abord
        if (!afficherFenetreConnexionBD()) {
            // Si la connexion échoue ou est annulée, quitter l'application
            System.exit(0);
            return;
        }

        // Charger la fenêtre principale de l'application
        Parent root = FXMLLoader.load(getClass().getResource("/victormarie/cogitoboard/fxml/VueKanban.fxml"));
        primaryStage.setTitle("CogitoBoard");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private boolean afficherFenetreConnexionBD() throws Exception {
        // Si une configuration existe déjà et qu'une connexion peut être établie, ne pas afficher la fenêtre
        ConfigurationBD config = ConfigurationBD.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/victormarie/cogitoboard/fxml/ConnexionBD.fxml"));
        Parent dialogRoot = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Connexion à la base de données");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));

        // Afficher le dialogue
        dialogStage.showAndWait();

        // Récupérer le résultat du contrôleur
        ControleurConnexionBD controleur = loader.getController();
        return controleur.isConnexionReussie();
    }

    public static void main(String[] args) {
        launch(args);
    }
}