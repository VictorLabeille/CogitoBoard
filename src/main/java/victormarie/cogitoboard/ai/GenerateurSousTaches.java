package victormarie.cogitoboard.ai;

import java.util.ArrayList;
import java.util.List;

public class GenerateurSousTaches{
    // Pour ce prototype, le comportement de l'IA est simulé

    public List<String> genererSousTaches(String descriptionTache) {

        List<String> sousTaches = new ArrayList<>();
        String lowerDesc = descriptionTache.toLowerCase();

        // Simuler l'IA en identifiant des types de tâches et en générant des sous-tâches pertinentes

        // Projet de développement
        if (lowerDesc.contains("développement") || lowerDesc.contains("programming") ||
                lowerDesc.contains("code") || lowerDesc.contains("application") ||
                lowerDesc.contains("logiciel") || lowerDesc.contains("développer")) {
            sousTaches.add("Créer le document des spécifications");
            sousTaches.add("Configurer l'environnement de développement");
            sousTaches.add("Implémenter les fonctionnalités de base");
            sousTaches.add("Rédiger les tests unitaires");
            sousTaches.add("Effectuer les tests d'intégration");
        }

        // Tâche de conception
        else if (lowerDesc.contains("design") || lowerDesc.contains("conception") ||
                lowerDesc.contains("interface") || lowerDesc.contains("ui") ||
                lowerDesc.contains("ux")) {
            sousTaches.add("Créer un wireframe");
            sousTaches.add("Rechercher des références et inspirations");
            sousTaches.add("Designer la maquette");
            sousTaches.add("Valider avec les parties prenantes");
            sousTaches.add("Finaliser les assets graphiques");
        }

        // Si aucune catégorie spécifique n'est détectée, proposer des sous-tâches génériques
        else {
            sousTaches.add("Définir les objectifs précis");
            sousTaches.add("Identifier les ressources nécessaires");
            sousTaches.add("Établir un planning");
            sousTaches.add("Exécuter la tâche principale");
            sousTaches.add("Réviser et finaliser");
        }

        return sousTaches;
    }

    /*
    // Exemple de code qui utiliserait un vrai modèle TensorFlow
    private SavedModelBundle model;
    private boolean isModelLoaded = false;

    public SubTaskGenerator() {
        try {
            // Charger le modèle TensorFlow (à remplacer par votre chemin de modèle)
            model = SavedModelBundle.load("path/to/subtask/model", "serve");
            isModelLoaded = true;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du modèle: " + e.getMessage());
            // Fallback vers la méthode de simulation si le modèle ne peut pas être chargé
        }
    }

    public List<String> generateSubTasksWithModel(String descriptionTache) {
        if (!isModelLoaded) {
            return generateSubTasks(descriptionTache); // Fallback vers la méthode de simulation
        }

        try {
            // Préparer les données d'entrée pour le modèle
            try (TString input = TString.scalarOfBytes(descriptionTache.getBytes(StandardCharsets.UTF_8))) {
                // Exécuter l'inférence
                ConcreteFunction function = model.function(Signature.DEFAULT);
                Tensor output = function.call(input);

                // Traiter la sortie du modèle
                byte[] outputBytes = output.asBytes().getByteArray();
                String outputText = new String(outputBytes, StandardCharsets.UTF_8);

                // Supposons que le modèle retourne les sous-tâches séparées par des sauts de ligne
                return Arrays.asList(outputText.split("\n"));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'inférence: " + e.getMessage());
            return generateSubTasks(descriptionTache); // Fallback en cas d'erreur
        }
    }
    */
}
