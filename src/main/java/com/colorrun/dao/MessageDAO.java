package com.colorrun.dao;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class MessageDAO {
    
    private DataSource dataSource;
    private UserDAO userDAO;
    
    public MessageDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.userDAO = new UserDAO();
    }
    
    public void save(Message message) throws SQLException {
        String sql = "INSERT INTO Message (expediteurId, destinataireId, date, contenu, lu) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getSender().getId());
            stmt.setInt(2, message.getReceiver().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(message.getDate()));
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
    
    public void markAsRead(int messageId) throws SQLException {
        String sql = "UPDATE Message SET lu = TRUE WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        }
    }
    
    public void markAllAsRead(int receiverId) throws SQLException {
        String sql = "UPDATE Message SET lu = TRUE WHERE destinataireId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.executeUpdate();
        }
    }
    
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
        message.setReceiver(receiver);
        
        message.setDate(rs.getTimestamp("date").toLocalDateTime());
        message.setContent(rs.getString("contenu"));
        message.setRead(rs.getBoolean("lu"));
        
        return message;
    }
}
