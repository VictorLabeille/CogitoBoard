package victormarie.cogitoboard.modele;

import java.time.LocalDateTime;
import java.util.UUID;

public class SousTache {
    private String id;
    private String tacheMere;
    private String titre;
    private boolean terminee;
    private LocalDateTime creation;
    private LocalDateTime miseajour;

    // Constructeur
    public SousTache(String tacheMere, String titre) {
        this.id = UUID.randomUUID().toString();
        this.tacheMere = tacheMere;
        this.titre = titre;
        this.terminee = false;
        this.creation = LocalDateTime.now();
        this.miseajour = LocalDateTime.now();
    }

    // Constructeur complet pour récupération depuis la BD
    public SousTache(String id, String tacheMere, String titre, boolean terminee,
                   LocalDateTime creation, LocalDateTime miseajour) {
        this.id = id;
        this.tacheMere = tacheMere;
        this.titre = titre;
        this.terminee = terminee;
        this.creation = creation;
        this.miseajour = miseajour;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getTacheMere() {
        return tacheMere;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
        this.miseajour = LocalDateTime.now();
    }

    public boolean estTerminee() {
        return terminee;
    }

    public void setTerminee(boolean terminee) {
        this.terminee = terminee;
        this.miseajour = LocalDateTime.now();
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public LocalDateTime getMiseajour() {
        return miseajour;
    }

    @Override
    public String toString() {
        return "SousTâche{" +
                "id='" + id + '\'' +
                ", titre='" + titre + '\'' +
                ", terminée=" + terminee +
                '}';
    }
}
