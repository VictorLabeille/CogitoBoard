package victormarie.cogitoboard.modele.dao;

import victormarie.cogitoboard.modele.Tache;
import victormarie.cogitoboard.modele.SousTache;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TacheDAO {

    public void saveTask(Tache tache) throws SQLException {
        String sql = "INSERT INTO Tache (id, titre, description, priorite, statut, creation, miseajour) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tache.getId());
            stmt.setString(2, tache.getTitre());
            stmt.setString(3, tache.getDescription());
            stmt.setString(4, tache.getPriorite().name());
            stmt.setString(5, tache.getStatut().name());
            stmt.setTimestamp(6, Timestamp.valueOf(tache.getCreation()));
            stmt.setTimestamp(7, Timestamp.valueOf(tache.getMiseajour()));

            stmt.executeUpdate();

            // Sauvegarde des sous-tâches
            SousTacheDAO SousTacheDAO = new SousTacheDAO();
            for (SousTache sousTache : tache.getSousTache()) {
                SousTacheDAO.sauvegarderSousTache(sousTache);
            }
        }
    }

    public void updateTask(Tache tache) throws SQLException {
        String sql = "UPDATE tache SET titre = ?, description = ?, priorite = ?, statut = ?, miseajour = ? " +
                "WHERE id = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tache.getTitre());
            stmt.setString(2, tache.getDescription());
            stmt.setString(3, tache.getPriorite().name());
            stmt.setString(4, tache.getStatut().name());
            stmt.setTimestamp(5, Timestamp.valueOf(tache.getMiseajour()));
            stmt.setString(6, tache.getId());

            stmt.executeUpdate();
        }
    }

    public void deleteTache(String tacheId) throws SQLException {
        // Les sous-tâches seront supprimées automatiquement grâce à ON DELETE CASCADE
        String sql = "DELETE FROM tache WHERE id = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tacheId);
            stmt.executeUpdate();
        }
    }

    public Tache getTacheById(String tacheId) throws SQLException {
        String sql = "SELECT * FROM tache WHERE id = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tacheId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Tache tache = createTacheFromResultSet(rs);

                    // Chargement des sous-tâches
                    SousTacheDAO SousTacheDAO = new SousTacheDAO();
                    List<SousTache> sousTaches = SousTacheDAO.getSousTachesByTacheId(tacheId);
                    for (SousTache sousTache : sousTaches) {
                        tache.addSousTache(sousTache);
                    }

                    return tache;
                }
            }
        }
        return null;
    }

    public List<Tache> getAllTasks() throws SQLException {
        List<Tache> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY updated_at DESC";

        try (Connection conn = ConnexionBD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            SousTacheDAO sousTacheDAO = new SousTacheDAO();

            while (rs.next()) {
                Tache tache = createTacheFromResultSet(rs);

                // Chargement des sous-tâches
                List<SousTache> sousTaches = sousTacheDAO.getSousTachesByTacheId(tache.getId());
                for (SousTache sousTache : sousTaches) {
                    tache.addSousTache(sousTache);
                }

                tasks.add(tache);
            }
        }
        return tasks;
    }

    public List<Tache> getTasksByStatus(Tache.Statut status) throws SQLException {
        List<Tache> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE status = ? ORDER BY updated_at DESC";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                SousTacheDAO sousTacheDAO = new SousTacheDAO();

                while (rs.next()) {
                    Tache tache = createTacheFromResultSet(rs);

                    // Chargement des sous-tâches
                    List<SousTache> sousTaches = sousTacheDAO.getSousTachesByTacheId(tache.getId());
                    for (SousTache sousTache : sousTaches) {
                        tache.addSousTache(sousTache);
                    }

                    tasks.add(tache);
                }
            }
        }
        return tasks;
    }

    private Tache createTacheFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String titre = rs.getString("titre");
        String description = rs.getString("description");
        Tache.Priorite priorite = Tache.Priorite.valueOf(rs.getString("priorite"));
        Tache.Statut statut = Tache.Statut.valueOf(rs.getString("statut"));
        LocalDateTime creation = rs.getTimestamp("creation").toLocalDateTime();
        LocalDateTime miseajour = rs.getTimestamp("miseajour").toLocalDateTime();

        return new Tache(id, titre, description, priorite, statut, creation, miseajour);
    }
}
