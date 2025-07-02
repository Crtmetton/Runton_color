package com.colorrun.dao;

import com.colorrun.business.Discussion;
import com.colorrun.business.User;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class DiscussionDAO {
    
    private DataSource dataSource;
    private UserDAO userDAO;
    
    public DiscussionDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Sauvegarde une nouvelle discussion dans la base de données
     */
    public void save(Discussion discussion) throws SQLException {
        String sql = "INSERT INTO Discussion (course_id, date, contenu, expediteurId) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, discussion.getCourseId());
            stmt.setTimestamp(2, Timestamp.valueOf(discussion.getDate()));
            stmt.setString(3, discussion.getContenu());
            stmt.setInt(4, discussion.getExpediteurId());
            
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    discussion.setId(keys.getInt(1));
                }
            }
        }
    }
    
    /**
     * Trouve une discussion par son ID
     */
    public Optional<Discussion> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Discussion WHERE id = ?";
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
    
    /**
     * Trouve tous les messages d'une course, triés par date (plus anciens en haut, plus récents en bas)
     */
    public List<Discussion> findByCourse(int courseId) throws SQLException {
        List<Discussion> discussions = new ArrayList<>();
        String sql = "SELECT * FROM Discussion WHERE course_id = ? ORDER BY date ASC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discussions.add(mapRow(rs));
                }
            }
        }
        return discussions;
    }
    
    /**
     * Trouve tous les messages d'un utilisateur
     */
    public List<Discussion> findByUser(int userId) throws SQLException {
        List<Discussion> discussions = new ArrayList<>();
        String sql = "SELECT * FROM Discussion WHERE expediteurId = ? ORDER BY date DESC";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    discussions.add(mapRow(rs));
                }
            }
        }
        return discussions;
    }
    
    /**
     * Compte le nombre de messages dans une course
     */
    public int countByCourse(int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Discussion WHERE course_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Supprime un message de discussion
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Discussion WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Met à jour le contenu d'un message de discussion
     */
    public void updateContent(int id, String newContent) throws SQLException {
        String sql = "UPDATE Discussion SET contenu = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newContent);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Supprime tous les messages d'une course
     */
    public void deleteByCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM Discussion WHERE course_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mappe une ligne de ResultSet vers un objet Discussion
     */
    private Discussion mapRow(ResultSet rs) throws SQLException {
        Discussion discussion = new Discussion();
        discussion.setId(rs.getInt("id"));
        discussion.setCourseId(rs.getInt("course_id"));
        discussion.setDate(rs.getTimestamp("date").toLocalDateTime());
        discussion.setContenu(rs.getString("contenu"));
        discussion.setExpediteurId(rs.getInt("expediteurId"));
        
        // Charger l'utilisateur expéditeur
        int expediteurId = rs.getInt("expediteurId");
        try {
            Optional<User> expediteur = userDAO.findById(expediteurId);
            if (expediteur.isPresent()) {
                discussion.setExpediteur(expediteur.get());
            }
        } catch (SQLException e) {
            // En cas d'erreur, on continue sans l'utilisateur
            System.err.println("Erreur lors du chargement de l'utilisateur " + expediteurId + ": " + e.getMessage());
        }
        
        return discussion;
    }
} 