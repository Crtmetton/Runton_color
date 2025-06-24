package com.colorrun.dao;

import com.colorrun.business.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import com.colorrun.config.DatabaseConfig;

public class UserDAO {
    
    private DataSource dataSource;
    
    public UserDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
    }
    
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO Utilisateur (nom, prenom, email, hashMotDePasse, role, photoProfile, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getLastName());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getPhotoUrl());
            stmt.setBoolean(7, user.isEnabled());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        }
    }
    
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
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
    
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Utilisateur";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }
    
    public void update(User user) throws SQLException {
        String sql = "UPDATE Utilisateur SET prenom = ?, nom = ?, email = ?, hashMotDePasse = ?, role = ?, photoProfile = ?, enabled = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getPhotoUrl());
            stmt.setBoolean(7, user.isEnabled());
            stmt.setInt(8, user.getId());
            stmt.executeUpdate();
        }
    }
    
    public void updateRole(int userId, String role) throws SQLException {
        String sql = "UPDATE Utilisateur SET role = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public boolean enableUserByToken(String token) throws SQLException {
        String sql = "SELECT user_id FROM verification_tokens WHERE token = ? AND expiry_date > NOW()";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String updateSql = "UPDATE Utilisateur SET enabled = TRUE WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        updateStmt.executeUpdate();
                    }
                    String deleteSql = "DELETE FROM verification_tokens WHERE token = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setString(1, token);
                        deleteStmt.executeUpdate();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public void saveVerificationToken(int userId, String token) throws SQLException {
        String sql = "INSERT INTO verification_tokens (user_id, token, expiry_date) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 24 HOUR))";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            stmt.executeUpdate();
        }
    }
    
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("prenom"));
        user.setLastName(rs.getString("nom"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("hashMotDePasse"));
        user.setRole(rs.getString("role"));
        user.setPhotoUrl(rs.getString("photoProfile"));
        user.setEnabled(true);
        return user;
    }
}
