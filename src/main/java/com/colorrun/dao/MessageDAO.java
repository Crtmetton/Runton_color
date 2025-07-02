package com.colorrun.dao;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * DAO dédié à la persistance des messages privés/publics (table MESSAGE).
 * <p>
 * Fournit les opérations CRUD ainsi que des méthodes utilitaires telles que le
 * comptage des messages non lus ou la mise à jour du statut de lecture. Toutes
 * les méthodes déclarent {@link SQLException} afin que la couche service
 * gère les erreurs de manière centralisée.
 * </p>
 */
public class MessageDAO {
    
    private DataSource dataSource;
    private UserDAO userDAO;
    
    public MessageDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Enregistre un nouveau message dans la base.
     *
     * @param message instance à sauvegarder (son ID sera mis à jour)
     * @throws SQLException si l'insertion échoue
     */
    public void save(Message message) throws SQLException {
        String sql = "INSERT INTO Message (expediteurId, destinataireId, date, contenu, lu) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getSender().getId());
            stmt.setInt(2, message.getRecipient().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));
            stmt.setString(4, message.getContent());
            stmt.setBoolean(5, message.isRead());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    message.setId(keys.getInt(1));
                }
            }
        }
    }
    
    /**
     * Recherche un message par son identifiant.
     *
     * @param id identifiant du message
     * @return {@link Optional} contenant le message si trouvé
     * @throws SQLException en cas d'erreur SQL
     */
    public Optional<Message> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Message WHERE id = ?";
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
     * Récupère la liste des messages envoyés par un utilisateur.
     *
     * @param senderId identifiant de l'expéditeur
     * @return liste des messages
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Message> findBySender(int senderId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Message WHERE expediteurId = ? ORDER BY date DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Récupère la liste des messages reçus par un utilisateur.
     *
     * @param receiverId identifiant du destinataire
     * @return liste des messages reçus
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Message> findByReceiver(int receiverId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Message WHERE destinataireId = ? ORDER BY date DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRow(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Compte le nombre de messages non lus pour un destinataire.
     *
     * @param receiverId identifiant du destinataire
     * @return nombre de messages non lus
     * @throws SQLException en cas d'erreur SQL
     */
    public int countUnreadByReceiver(int receiverId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Message WHERE destinataireId = ? AND lu = FALSE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Marque un message comme lu.
     *
     * @param messageId identifiant du message
     * @throws SQLException en cas d'erreur SQL
     */
    public void markAsRead(int messageId) throws SQLException {
        String sql = "UPDATE Message SET lu = TRUE WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Marque tous les messages reçus par un utilisateur comme lus.
     *
     * @param receiverId identifiant du destinataire
     * @throws SQLException en cas d'erreur SQL
     */
    public void markAllAsRead(int receiverId) throws SQLException {
        String sql = "UPDATE Message SET lu = TRUE WHERE destinataireId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Supprime un message.
     *
     * @param id identifiant du message à supprimer
     * @throws SQLException en cas d'erreur SQL
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Message WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Message mapRow(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        
        int senderId = rs.getInt("expediteurId");
        User sender = userDAO.findById(senderId).orElse(new User());
        message.setSender(sender);
        
        int receiverId = rs.getInt("destinataireId");
        User receiver = userDAO.findById(receiverId).orElse(new User());
        message.setRecipient(receiver);
        
        message.setTimestamp(rs.getTimestamp("date").toLocalDateTime());
        message.setContent(rs.getString("contenu"));
        message.setRead(rs.getBoolean("lu"));
        
        return message;
    }
}
