package victormarie.cogitoboard.modele;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tache {
    private String id;
    private String titre;
    private String description;
    private Priorite priorite;
    private Statut statut;
    private LocalDateTime creation;
    private LocalDateTime miseajour;
    private List<SousTache> sousTache;

    public enum Priorite {
        LOW, MEDIUM, HIGH
    }

    public enum Statut {
        TODO, IN_PROGRESS, DONE
    }

    // Constructeur rapide
    public Tache(String titre, String description) {
        this.id = UUID.randomUUID().toString();
        this.titre = titre;
        this.description = description;
        this.priorite = Priorite.MEDIUM;
        this.statut = Statut.TODO;
        this.creation = LocalDateTime.now();
        this.miseajour = LocalDateTime.now();
        this.sousTache = new ArrayList<>();
    }

    // Constructeur complet pour récupération depuis la BD
    public Tache(String id, String titre, String description, Priorite priorite,
                Statut statut, LocalDateTime creation, LocalDateTime miseajour) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.statut = statut;
        this.creation = creation;
        this.miseajour = miseajour;
        this.sousTache = new ArrayList<>();
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
        this.miseajour = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.miseajour = LocalDateTime.now();
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
        this.miseajour = LocalDateTime.now();
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
        this.miseajour = LocalDateTime.now();
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public LocalDateTime getMiseajour() {
        return miseajour;
    }

    public List<SousTache> getSousTache() {
        return sousTache;
    }

    public void addSubTask(SousTache subTask) {
        this.sousTache.add(subTask);
        this.miseajour = LocalDateTime.now();
    }

    public void removeSubTask(SousTache subTask) {
        this.sousTache.remove(subTask);
        this.miseajour = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Tâche{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", statut=" + statut +
                ", priorite=" + priorite +
                '}';
    }
}
