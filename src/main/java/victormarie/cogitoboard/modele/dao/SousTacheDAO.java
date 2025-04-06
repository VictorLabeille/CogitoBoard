package victormarie.cogitoboard.modele.dao;

import victormarie.cogitoboard.modele.SousTache;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SousTacheDAO {

    public void sauvegarderSousTache(SousTache sousTache) throws SQLException {
        String sql = "INSERT INTO sousTache (id, tacheMere, titre, terminee, creation, miseajour) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sousTache.getId());
            stmt.setString(2, sousTache.getTacheMere());
            stmt.setString(3, sousTache.getTitre());
            stmt.setBoolean(4, sousTache.estTerminee());
            stmt.setTimestamp(5, Timestamp.valueOf(sousTache.getCreation()));
            stmt.setTimestamp(6, Timestamp.valueOf(sousTache.getMiseajour()));

            stmt.executeUpdate();
        }
    }

    public void updateSousTache(SousTache sousTache) throws SQLException {
        String sql = "UPDATE sousTache SET titre = ?, terminee = ?, miseajour = ? WHERE id = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sousTache.getTitre());
            stmt.setBoolean(2, sousTache.estTerminee());
            stmt.setTimestamp(3, Timestamp.valueOf(sousTache.getMiseajour()));
            stmt.setString(4, sousTache.getId());

            stmt.executeUpdate();
        }
    }

    public void deleteSousTache(String idSousTache) throws SQLException {
        String sql = "DELETE FROM sousTache WHERE id = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idSousTache);
            stmt.executeUpdate();
        }
    }

    public void deleteSousTachesByTacheId(String idTache) throws SQLException {
        String sql = "DELETE FROM sousTache WHERE tacheMere = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idTache);
            stmt.executeUpdate();
        }
    }

    public SousTache getSousTacheById(String idSousTache) throws SQLException {
        String sql = "SELECT * FROM sousTache WHERE id = ?";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idSousTache);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createSousTacheFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<SousTache> getSousTachesByTacheId(String idTache) throws SQLException {
        List<SousTache> subTasks = new ArrayList<>();
        String sql = "SELECT * FROM sousTache WHERE tacheMere = ? ORDER BY creation ASC";

        try (Connection conn = ConnexionBD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idTache);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subTasks.add(createSousTacheFromResultSet(rs));
                }
            }
        }
        return subTasks;
    }

    private SousTache createSousTacheFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String tacheMere = rs.getString("tacheMere");
        String titre = rs.getString("titre");
        boolean terminee = rs.getBoolean("terminee");
        LocalDateTime creation = rs.getTimestamp("creation").toLocalDateTime();
        LocalDateTime miseajour = rs.getTimestamp("miseajour").toLocalDateTime();

        return new SousTache(id, tacheMere, titre, terminee, creation, miseajour);
    }
}
