package victormarie.cogitoboard.controleur;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import victormarie.cogitoboard.modele.dao.ConfigurationBD;
import victormarie.cogitoboard.modele.dao.ConnexionBD;

import java.sql.Connection;
import java.sql.SQLException;

public class ControleurConnexionBD {

    @FXML private TextField urlField;
    @FXML private TextField utilisateurField;
    @FXML private PasswordField motDePasseField;
    @FXML private CheckBox sauvegarderConfigCheckBox;

    private boolean connexionReussie = false;

    @FXML
    public void initialize() {
        // Charger la configuration existante
        ConfigurationBD config = ConfigurationBD.getInstance();
        urlField.setText(config.getUrl());
        utilisateurField.setText(config.getUtilisateur());
        motDePasseField.setText(config.getMotDePasse());
    }

    @FXML
    public void testerConnexion() {
        String url = urlField.getText().trim();
        String utilisateur = utilisateurField.getText().trim();
        String motDePasse = motDePasseField.getText();

        try {
            Connection conn = ConnexionBD.testerConnexion(url, utilisateur, motDePasse);
            conn.close();

            showAlert(Alert.AlertType.INFORMATION, "Connexion réussie",
                    "La connexion à la base de données a été établie avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Échec de la connexion",
                    "Impossible de se connecter à la base de données: " + e.getMessage());
        }
    }

    @FXML
    public void validerConnexion() {
        String url = urlField.getText().trim();
        String utilisateur = utilisateurField.getText().trim();
        String motDePasse = motDePasseField.getText();

        // Vérifier que les champs ne sont pas vides
        if (url.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "L'URL de la base de données est requise.");
            return;
        }

        try {
            // Tester la connexion
            Connection conn = ConnexionBD.testerConnexion(url, utilisateur, motDePasse);
            conn.close();

            // Mettre à jour la configuration
            ConfigurationBD config = ConfigurationBD.getInstance();
            config.setUrl(url);
            config.setUtilisateur(utilisateur);
            config.setMotDePasse(motDePasse);

            // Sauvegarder si demandé
            if (sauvegarderConfigCheckBox.isSelected()) {
                config.sauvegarderConfiguration();
            }

            // Mettre à jour l'état de connexion
            config.setEstConnecte(true);
            connexionReussie = true;

            // Fermer la fenêtre
            ((Stage) urlField.getScene().getWindow()).close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Échec de la connexion",
                    "Impossible de se connecter à la base de données: " + e.getMessage());
        }
    }

    @FXML
    public void annuler() {
        // Fermer la fenêtre sans sauvegarder
        ((Stage) urlField.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean isConnexionReussie() {
        return connexionReussie;
    }
}