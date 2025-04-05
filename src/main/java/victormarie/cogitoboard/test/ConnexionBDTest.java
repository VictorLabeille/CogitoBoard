package victormarie.cogitoboard.test;

import victormarie.cogitoboard.modele.dao.ConnexionBD;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnexionBDTest {
    public static void main(String[] args) {
        try {
            Connection conn = ConnexionBD.getConnexion();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connexion réussie à la base de données !");
            } else {
                System.out.println("Échec de la connexion à la base de données.");
            }

            ConnexionBD.closeConnexion();
            if (conn.isClosed()) {
                System.out.println("Connexion fermée correctement.");
            } else {
                System.out.println("La connexion n'a pas été fermée correctement.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
