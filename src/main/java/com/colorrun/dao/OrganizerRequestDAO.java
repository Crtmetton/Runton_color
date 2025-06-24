package com.colorrun.dao;

import com.colorrun.business.OrganizerRequest;
import com.colorrun.business.User;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class OrganizerRequestDAO {
    
    private DataSource dataSource;
    private UserDAO userDAO;
    
    public OrganizerRequestDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.userDAO = new UserDAO();
    }
    
    public void save(OrganizerRequest request) throws SQLException {
        String sql = "INSERT INTO DemandeOrganisateur (utilisateurId, date, motivations, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getUser().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(request.getDate()));
            stmt.setString(3, request.getMotivation());
            stmt.setString(4, request.getStatus());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    request.setId(keys.getInt(1));
                }
            }
        }
    }
    
    public Optional<OrganizerRequest> findById(int id) throws SQLException {
        String sql = "SELECT * FROM DemandeOrganisateur WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public Optional<OrganizerRequest> findByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM DemandeOrganisateur WHERE utilisateurId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public List<OrganizerRequest> findAll() throws SQLException {
        List<OrganizerRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM DemandeOrganisateur ORDER BY date DESC";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
        }
        return requests;
    }
    
    public List<OrganizerRequest> findByStatus(String status) throws SQLException {
        List<OrganizerRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM DemandeOrganisateur WHERE statut = ? ORDER BY date DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
            }
        }
        return requests;
    }
    
    public void update(OrganizerRequest request) throws SQLException {
        String sql = "UPDATE DemandeOrganisateur SET utilisateurId = ?, date = ?, motivations = ?, statut = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getUser().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(request.getDate()));
            stmt.setString(3, request.getMotivation());
            stmt.setString(4, request.getStatus());
            stmt.setInt(5, request.getId());
            stmt.executeUpdate();
        }
    }
    
    public void updateStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE DemandeOrganisateur SET statut = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM DemandeOrganisateur WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private OrganizerRequest mapRow(ResultSet rs) throws SQLException {
        OrganizerRequest request = new OrganizerRequest();
        request.setId(rs.getInt("id"));
        
        int userId = rs.getInt("utilisateurId");
        User user = userDAO.findById(userId).orElse(new User());
        request.setUser(user);
        
        request.setDate(rs.getTimestamp("date").toLocalDateTime());
        request.setMotivation(rs.getString("motivations"));
        request.setStatus(rs.getString("statut"));
        
        return request;
    }
}
