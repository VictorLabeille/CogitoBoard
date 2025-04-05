package victormarie.cogitoboard.controleur;

import victormarie.cogitoboard.modele.dao.SousTacheDAO;
import victormarie.cogitoboard.modele.Tache;
import victormarie.cogitoboard.modele.SousTache;
import victormarie.cogitoboard.ai.GenerateurSousTaches;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControleurDialogueTache {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Tache.Priorite> priorityComboBox;
    @FXML private ComboBox<Tache.Statut> statusComboBox;
    @FXML private TextField newSubTaskField;
    @FXML private ListView<SousTache> subTasksList;

    private Tache tache;
    private ObservableList<SousTache> sousTaches = FXCollections.observableArrayList();
    private SousTacheDAO sousTacheDAO = new SousTacheDAO();
    private GenerateurSousTaches GenerateurSousTaches;

    @FXML
    public void initialize() {
        // Initialiser les combobox
        priorityComboBox.setItems(FXCollections.observableArrayList(Tache.Priorite.values()));
        statusComboBox.setItems(FXCollections.observableArrayList(Tache.Statut.values()));

        // Configurer la liste des sous-tâches
        subTasksList.setItems(sousTaches);
        setupSubTaskCellFactory();
    }

    public void setTache(Tache tache) {
        this.tache = tache;

        // Remplir les champs avec les valeurs de la tâche
        titleField.setText(tache.getTitre());
        descriptionArea.setText(tache.getDescription());
        priorityComboBox.setValue(tache.getPriorite());
        statusComboBox.setValue(tache.getStatut());

        // Charger les sous-tâches
        sousTaches.clear();
        sousTaches.addAll(tache.getSousTache());
    }

    public void setGenerateurSousTaches(GenerateurSousTaches generator) {
        this.GenerateurSousTaches = generator;
    }

    @FXML
    public void addSousTache() {
        String title = newSubTaskField.getText().trim();
        if (!title.isEmpty()) {
            // Créer une nouvelle sous-tâche
            SousTache sousTache = new SousTache(tache.getId(), title);

            // Ajouter à la liste
            sousTaches.add(sousTache);

            // Vider le champ
            newSubTaskField.clear();
            newSubTaskField.requestFocus();
        }
    }

    @FXML
    public void GenerateurSousTachesAvecIA() {
        String taskTitle = titleField.getText().trim();
        String taskDesc = descriptionArea.getText().trim();

        if (taskTitle.isEmpty()) {
            showAlert("Attention", "Le titre de la tâche est vide",
                    "Veuillez entrer un titre pour pouvoir générer des sous-tâches.");
            return;
        }

        // Afficher une alerte de chargement
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION);
        loadingAlert.setTitle("Génération en cours");
        loadingAlert.setHeaderText("Génération des sous-tâches");
        loadingAlert.setContentText("L'IA est en train de générer des sous-tâches...");

        // Lancer la génération dans un thread séparé pour ne pas bloquer l'UI
        new Thread(() -> {
            try {
                // Combiner le titre et la description pour donner du contexte à l'IA
                String taskContext = taskTitle;
                if (!taskDesc.isEmpty()) {
                    taskContext += "\n\n" + taskDesc;
                }

                // Générer les sous-tâches
                List<String> suggestions = GenerateurSousTaches.genererSousTaches(taskContext);

                // Mettre à jour l'UI dans le thread JavaFX
                Platform.runLater(() -> {
                    loadingAlert.close();

                    if (suggestions.isEmpty()) {
                        showAlert("Information", "Aucune suggestion",
                                "L'IA n'a pas pu générer de sous-tâches pour cette tâche.");
                    } else {
                        // Ajouter les suggestions comme sous-tâches
                        for (String suggestion : suggestions) {
                            SousTache sousTache = new SousTache(tache.getId(), suggestion);
                            sousTaches.add(sousTache);
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingAlert.close();
                    showAlert("Erreur", "Erreur de génération",
                            "Une erreur est survenue lors de la génération des sous-tâches: " + e.getMessage());
                });
            }
        }).start();

        loadingAlert.show();
    }

    private void setupSubTaskCellFactory() {
        subTasksList.setCellFactory(param -> new ListCell<SousTache>() {
            private final CheckBox checkBox = new CheckBox();
            private final Label label = new Label();
            private final Button deleteButton = new Button("×");

            {
                // Configurer le bouton de suppression
                deleteButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #dc3545;");
                deleteButton.setOnAction(event -> {
                    SousTache subTask = getItem();
                    if (subTask != null) {
                        sousTaches.remove(subTask);
                    }
                });
            }

            @Override
            protected void updateItem(SousTache item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Configurer la checkbox
                    checkBox.setSelected(item.estTerminee());
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        item.setTerminee(newVal);
                        updateStyles();
                    });

                    // Configurer le label
                    label.setText(item.getTitre());
                    updateStyles();

                    // Créer le layout
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(10);
                    hbox.getChildren().addAll(checkBox, label);

                    // Ajouter le spacer et le bouton de suppression
                    javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
                    javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    hbox.getChildren().addAll(spacer, deleteButton);

                    setGraphic(hbox);
                }
            }

            private void updateStyles() {
                SousTache item = getItem();
                if (item != null && item.estTerminee()) {
                    label.getStyleClass().add("sousTache-terminee");
                } else {
                    label.getStyleClass().removeAll("sousTache-terminee");
                }
            }
        });
    }

    public void updateTaskFromInputs() {
        // Mettre à jour la tâche avec les valeurs des champs
        tache.setTitre(titleField.getText().trim());
        tache.setDescription(descriptionArea.getText().trim());
        tache.setPriorite(priorityComboBox.getValue());
        tache.setStatut(statusComboBox.getValue());

        // Gérer les sous-tâches
        try {
            // 1. Trouver les sous-tâches supprimées (qui étaient dans la base mais plus dans la liste)
            List<SousTache> originalSubTasks = new ArrayList<>(tache.getSousTache());
            for (SousTache original : originalSubTasks) {
                boolean found = false;
                for (SousTache current : sousTaches) {
                    if (original.getId().equals(current.getId())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // La sous-tâche a été supprimée, la retirer de la tâche et de la base
                    tache.deleteSousTache(original);
                    sousTacheDAO.deleteSousTache(original.getId());
                }
            }

            // 2. Mettre à jour ou ajouter les sous-tâches actuelles
            for (SousTache subTask : sousTaches) {
                // Vérifier si c'est une sous-tâche temporaire (créée avant que la tâche n'ait un ID)
                if ("temp".equals(subTask.getId())) {
                    // Mettre à jour l'ID de la sous-tâche avec l'ID réel de la tâche
                    subTask.setId(tache.getId()); // Cela supposerait que vous avez une méthode setId dans SousTache

                    // Nouvelle sous-tâche, l'ajouter à la tâche et à la base
                    tache.addSousTache(subTask);
                    sousTacheDAO.sauvegarderSousTache(subTask);
                } else {
                    boolean isNew = subTask.getId().length() == 36 && !tache.getSousTache().contains(subTask);

                    if (isNew) {
                        // Nouvelle sous-tâche, l'ajouter à la tâche et à la base
                        tache.addSousTache(subTask);
                        sousTacheDAO.sauvegarderSousTache(subTask);
                    } else {
                        // Sous-tâche existante, la mettre à jour dans la base
                        sousTacheDAO.updateSousTache(subTask);
                    }
                }
            }
        } catch (SQLException e) {
            // Afficher une erreur mais continuer (la tâche sera sauvegardée sans toutes les sous-tâches)
            showAlert("Erreur", "Erreur lors de la sauvegarde des sous-tâches", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /*public void setResult(Callback<ButtonType, Boolean> resultConverter) {
        DialogPane dialogPane = subTasksList.getScene().getWindow().getScene().getRoot();
        Dialog<?> dialog = (Dialog<?>) dialogPane.getScene().getWindow();

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                updateTaskFromInputs();
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        });
    }*/
}
