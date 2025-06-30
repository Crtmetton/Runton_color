package com.colorrun.service.impl;

import com.colorrun.service.VerificationTokenService;
import com.colorrun.business.User;
import com.colorrun.dao.UserDAO;
import com.colorrun.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implémentation du service de vérification de tokens.
 * 
 * Cette classe gère les tokens de vérification d'email avec une table
 * dédiée en base de données et une durée de validité configurable.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class VerificationTokenServiceImpl implements VerificationTokenService {
    
    private static final int TOKEN_EXPIRY_HOURS = 24;
    
    private final UserDAO userDAO;
    
    public VerificationTokenServiceImpl() {
        this.userDAO = new UserDAO();
    }
    
    @Override
    public String generateVerificationToken(User user) throws Exception {
        // Générer un token unique
        String token = UUID.randomUUID().toString();
        
        // Supprimer les anciens tokens de cet utilisateur
        deleteExistingTokensForUser(user.getId());
        
        // Calculer la date d'expiration
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);
        
        // Sauvegarder le token en base
        String sql = "INSERT INTO VerificationToken (user_id, token, expiry_date) VALUES (?, ?, ?)";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, user.getId());
            statement.setString(2, token);
            statement.setTimestamp(3, Timestamp.valueOf(expiryDate));
            
            statement.executeUpdate();
        }
        
        return token;
    }
    
    @Override
    public boolean validateToken(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        String sql = """
            SELECT vt.user_id, vt.expiry_date 
            FROM VerificationToken vt 
            WHERE vt.token = ? AND vt.expiry_date > ?
            """;
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, token);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    
                    // Activer le compte utilisateur
                    activateUser(userId);
                    
                    // Supprimer le token utilisé
                    deleteToken(token);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void cleanupExpiredTokens() throws Exception {
        String sql = "DELETE FROM VerificationToken WHERE expiry_date < ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            int deletedCount = statement.executeUpdate();
            System.out.println("Tokens expirés supprimés : " + deletedCount);
        }
    }
    
    @Override
    public boolean isTokenValid(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM VerificationToken WHERE token = ? AND expiry_date > ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, token);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Supprime les tokens existants pour un utilisateur.
     */
    private void deleteExistingTokensForUser(int userId) throws SQLException {
        String sql = "DELETE FROM VerificationToken WHERE user_id = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
    
    /**
     * Active un compte utilisateur.
     */
    private void activateUser(int userId) throws Exception {
        // Utiliser le UserDAO pour activer l'utilisateur
        var userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);
            userDAO.update(user);
        }
    }
    
    /**
     * Supprime un token spécifique.
     */
    private void deleteToken(String token) throws SQLException {
        String sql = "DELETE FROM VerificationToken WHERE token = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, token);
            statement.executeUpdate();
        }
    }
} 