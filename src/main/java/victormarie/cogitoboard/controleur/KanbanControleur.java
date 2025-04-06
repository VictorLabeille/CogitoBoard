package victormarie.cogitoboard.controleur;

import victormarie.cogitoboard.modele.dao.TacheDAO;
import victormarie.cogitoboard.modele.dao.SousTacheDAO;
import victormarie.cogitoboard.modele.Tache;
import victormarie.cogitoboard.modele.SousTache;
import victormarie.cogitoboard.ai.ClassificateurTache;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class KanbanControleur {

    @FXML
    private VBox ColonneAFaire;

    @FXML
    private VBox ColonneEnCours;

    @FXML
    private VBox ColonneTerminee;

    @FXML
    private Label labelStatut;

    private TacheDAO tacheDAO = new TacheDAO();
    private SousTacheDAO sousTacheDAO = new SousTacheDAO();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        loadTaches();
    }

    /**
     * Initialise le tableau Kanban en chargeant toutes les tâches
     */
    public void loadTaches() {
        try {
            // Vider les colonnes
            ColonneAFaire.getChildren().clear();
            ColonneEnCours.getChildren().clear();
            ColonneTerminee.getChildren().clear();

            // Charger les tâches par statut
            loadTachesByStatut(Tache.Statut.TODO, ColonneAFaire);
            loadTachesByStatut(Tache.Statut.IN_PROGRESS, ColonneEnCours);
            loadTachesByStatut(Tache.Statut.DONE, ColonneTerminee);

            setupDragAndDrop();

            labelStatut.setText("Tâches chargées avec succès");
        } catch (SQLException e) {
            showErrorAlert("Erreur lors du chargement des tâches", e.getMessage());
            labelStatut.setText("Erreur lors du chargement des tâches");
        }
    }

    /**
     * Charge les tâches d'un statut spécifique dans une colonne
     */
    private void loadTachesByStatut(Tache.Statut statut, VBox colonne) throws SQLException {
        List<Tache> taches = tacheDAO.getTachesByStatut(statut);

        for (Tache tache : taches) {
            Node tacheCard = createTacheCard(tache);
            colonne.getChildren().add(tacheCard);
        }
    }

    /**
     * Crée une carte visuelle pour une tâche
     */
    private Node createTacheCard(Tache tache) {
        VBox tacheCard = new VBox(5);
        tacheCard.getStyleClass().add("tache-card");
        tacheCard.setUserData(tache); // Stocker la tâche dans le nœud pour le drag & drop

        // Définir la classe CSS selon la priorité
        switch (tache.getPriorite()) {
            case HIGH:
                tacheCard.getStyleClass().add("priorite-high");
                break;
            case MEDIUM:
                tacheCard.getStyleClass().add("priorite-medium");
                break;
            case LOW:
                tacheCard.getStyleClass().add("priorite-low");
                break;
        }

        // Titre de la tâche
        Label titreLabel = new Label(tache.getTitre());
        titreLabel.getStyleClass().add("tache-titre");

        // Description (si présente)
        Label descLabel = null;
        if (tache.getDescription() != null && !tache.getDescription().isEmpty()) {
            String shortDesc = tache.getDescription().length() > 50
                    ? tache.getDescription().substring(0, 47) + "..."
                    : tache.getDescription();
            descLabel = new Label(shortDesc);
            descLabel.getStyleClass().add("tache-description");
            descLabel.setWrapText(true);
        }

        // Sous-tâches (compteur)
        Label sousTacheLabel = new Label(getSousTacheProgress(tache));
        sousTacheLabel.getStyleClass().add("tache-meta");

        // Dernière mise à jour
        Label updateLabel = new Label("Creation: " + tache.getCreation().format(formatter));
        updateLabel.getStyleClass().add("tache-meta");

        // Boutons d'action
        Button editButton = new Button("Éditer");
        editButton.setOnAction(e -> editTache(tache));

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> deleteTache(tache));

        HBox actionsBox = new HBox(5, editButton, deleteButton);

        // Ajouter tous les éléments à la carte
        tacheCard.getChildren().add(titreLabel);
        if (descLabel != null) {
            tacheCard.getChildren().add(descLabel);
        }
        tacheCard.getChildren().addAll(sousTacheLabel, updateLabel, actionsBox);

        // Configuration du drag & drop
        setupDragAndDropForTache(tacheCard);

        return tacheCard;
    }

    /**
     * Obtient le progrès des sous-tâches pour affichage
     */
    private String getSousTacheProgress(Tache tache) {
        List<SousTache> sousTache = tache.getSousTache();
        if (sousTache.isEmpty()) {
            return "Aucune sous-tâche";
        }

        long terminees = sousTache.stream().filter(SousTache::estTerminee).count();
        return String.format("Sous-tâches: %d/%d", terminees, sousTache.size());
    }

    /**
     * Configure le système de drag & drop pour les colonnes
     */
    private void setupDragAndDrop() {
        setupDropZone(ColonneAFaire, Tache.Statut.TODO);
        setupDropZone(ColonneEnCours, Tache.Statut.IN_PROGRESS);
        setupDropZone(ColonneTerminee, Tache.Statut.DONE);
    }

    /**
     * Configure le drag & drop pour une carte de tâche
     */
    private void setupDragAndDropForTache(Node tacheCard) {
        tacheCard.setOnDragDetected(event -> {
            Dragboard db = tacheCard.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("tache"); // Identifiant pour indiquer qu'on déplace une tâche
            db.setContent(content);

            // Stocke la source du drag & drop
            event.consume();
        });
    }

    /**
     * Configure une colonne comme zone de dépôt pour le drag & drop
     */
    private void setupDropZone(VBox column, Tache.Statut statut) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column &&
                    event.getDragboard().hasString() &&
                    "tache".equals(event.getDragboard().getString())) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean succes = false;

            if (db.hasString() && "tache".equals(db.getString())) {
                Node sourceNode = (Node) event.getGestureSource();
                Tache tache = (Tache) sourceNode.getUserData();

                // Mettre à jour le statut de la tâche
                try {
                    tache.setStatut(statut);
                    tacheDAO.updateTache(tache);

                    // Recharger toutes les tâches
                    loadTaches();
                    succes = true;
                } catch (SQLException e) {
                    showErrorAlert("Erreur lors du déplacement de la tâche", e.getMessage());
                }
            }

            event.setDropCompleted(succes);
            event.consume();
        });
    }

    /**
     * Ouvre le dialogue de création d'une nouvelle tâche
     */
    @FXML
    private void showDialogueNouvelleTache() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/victormarie/cogitoboard/fxml/DialogueTache.fxml"));
            DialogPane dialogPane = loader.load();

            ControleurDialogueTache controleur = loader.getController();

            // Créer une nouvelle tâche temporaire et la définir dans le contrôleur
            Tache nouvelleTache = new Tache("", "");
            controleur.setTache(nouvelleTache);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Nouvelle tâche");

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controleur.updateTaskFromInputs();
                tacheDAO.saveTache(nouvelleTache);
                labelStatut.setText("Tâche créée avec succès");
            }
        } catch (IOException | SQLException e) {
            showErrorAlert("Erreur lors de la création de la tâche", e.getMessage());
        }
        loadTaches();
    }

    /**
     * Ouvre le dialogue d'édition d'une tâche existante
     */
    private void editTache(Tache tache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/victormarie/cogitoboard/fxml/DialogueTache.fxml"));
            DialogPane dialogPane = loader.load();

            ControleurDialogueTache controleur = loader.getController();
            controleur.setTache(tache);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Modifier la tâche");

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controleur.updateTaskFromInputs();
                tacheDAO.updateTache(tache);
                loadTaches();
                labelStatut.setText("Tâche mise à jour avec succès");
            }
        } catch (IOException | SQLException e) {
            showErrorAlert("Erreur lors de la modification de la tâche", e.getMessage());
        }
    }

    /**
     * Supprime une tâche après confirmation
     */
    private void deleteTache(Tache tache) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Supprimer la tâche");
        confirmDialog.setHeaderText("Êtes-vous sûr de vouloir supprimer cette tâche ?");
        confirmDialog.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                tacheDAO.deleteTache(tache.getId());
                sousTacheDAO.deleteSousTachesByTacheId(tache.getId());
                loadTaches();
                labelStatut.setText("Tâche supprimée avec succès");
            } catch (SQLException e) {
                showErrorAlert("Erreur lors de la suppression de la tâche", e.getMessage());
            }
        }
    }

    /**
     * Utilise l'IA pour classer automatiquement les tâches par priorité
     */
    @FXML
    private void classerTachesAvecAI() {
        try {
            ClassificateurTache classifieur = new ClassificateurTache();
            List<Tache> tasks = tacheDAO.getToutesLesTaches();

            // Classification par lot
            classifieur.classerTaches(tasks);

            // Mettre à jour chaque tâche dans la BD
            for (Tache tache : tasks) {
                tacheDAO.updateTache(tache);
            }

            // Recharger les tâches
            loadTaches();
            labelStatut.setText("Tâches classifiées par IA avec succès");
        } catch (Exception e) {
            showErrorAlert("Erreur lors de la classification par IA", e.getMessage());
            labelStatut.setText("Échec de la classification par IA");
        }
    }

    @FXML
    private void actualiser(){
        loadTaches();
    }

    /**
     * Affiche une boîte de dialogue d'erreur
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
