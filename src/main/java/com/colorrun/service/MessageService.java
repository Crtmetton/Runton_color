package com.colorrun.service;

import com.colorrun.business.Message;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MessageService {
    
    void sendMessage(int senderId, int receiverId, String content) throws SQLException;
    
    List<Message> getReceivedMessages(int userId) throws SQLException;
    
    List<Message> getSentMessages(int userId) throws SQLException;
    
    int getUnreadMessageCount(int userId) throws SQLException;
    
    void markAsRead(int messageId) throws SQLException;
    
    void markAllAsRead(int userId) throws SQLException;
    
    void deleteMessage(int messageId) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    void create(int courseId, int userId, String content) throws SQLException;
    
    List<Message> findByCourse(int courseId) throws SQLException;
    
    Optional<Message> findById(int id) throws SQLException;
    
    void delete(int id) throws SQLException;
} 