package victormarie.cogitoboard.modele.dao;

import victormarie.cogitoboard.modele.dao.ConfigurationBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBD {
    private static Connection connexion;

    public static Connection getConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            try {
                // Utiliser la configuration actuelle
                ConfigurationBD config = ConfigurationBD.getInstance();
                Class.forName("com.mysql.cj.jdbc.Driver");
                connexion = DriverManager.getConnection(
                        config.getUrl(),
                        config.getUtilisateur(),
                        config.getMotDePasse());
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connexion;
    }

    public static Connection testerConnexion(String url, String utilisateur, String motDePasse) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, utilisateur, motDePasse);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    public static void closeConnexion() {
        if (connexion != null) {
            try {
                connexion.close();
                connexion = null;
            } catch (SQLException e) {
                System.err.println("Erreur à la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
}