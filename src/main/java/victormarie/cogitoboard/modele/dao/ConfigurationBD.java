package victormarie.cogitoboard.modele.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationBD {
    private String url;
    private String utilisateur;
    private String motDePasse;
    private boolean estConnecte;

    private static final String FICHIER_CONFIG = "config.properties";
    private static ConfigurationBD instance;

    private ConfigurationBD() {
        // Par défaut
        this.url = "jdbc:mysql://localhost:3306/CogitoBoard";
        this.utilisateur = "root";
        this.motDePasse = "";
        this.estConnecte = false;
        chargerConfiguration();
    }

    public static ConfigurationBD getInstance() {
        if (instance == null) {
            instance = new ConfigurationBD();
        }
        return instance;
    }

    public void sauvegarderConfiguration() {
        Properties props = new Properties();
        props.setProperty("url", url);
        props.setProperty("utilisateur", utilisateur);
        props.setProperty("motDePasse", motDePasse);

        try (FileOutputStream output = new FileOutputStream(FICHIER_CONFIG)) {
            props.store(output, "Configuration de connexion à la base de données");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration: " + e.getMessage());
        }
    }

    public void chargerConfiguration() {
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(FICHIER_CONFIG)) {
            props.load(input);
            this.url = props.getProperty("url", this.url);
            this.utilisateur = props.getProperty("utilisateur", this.utilisateur);
            this.motDePasse = props.getProperty("motDePasse", this.motDePasse);
        } catch (IOException e) {
            // Le fichier n'existe pas encore, on utilisera les valeurs par défaut
            System.out.println("Aucun fichier de configuration trouvé, utilisation des valeurs par défaut");
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public boolean estConnecte() {
        return estConnecte;
    }

    public void setEstConnecte(boolean estConnecte) {
        this.estConnecte = estConnecte;
    }
}
