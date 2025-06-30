package com.colorrun.service.impl;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import com.colorrun.dao.MessageDAO;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.MessageService;
import com.colorrun.util.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageServiceImpl implements MessageService {
    
    private final MessageDAO messageDAO;
    private UserDAO userDAO;
    
    public MessageServiceImpl() {
        this.messageDAO = new MessageDAO();
        this.userDAO = new UserDAO();
    }
    
    public void sendMessage(User sender, User recipient, String content) {
        try {
            Logger.info("MessageService", "Envoi message de " + sender.getFullName() + " vers " + recipient.getFullName());
            
            Message message = new Message();
            message.setSender(sender);
            message.setRecipient(recipient);
            message.setContent(content);
            message.setTimestamp(LocalDateTime.now());
            message.setType("PRIVATE");
            
            messageDAO.save(message);
            Logger.success("MessageService", "Message envoyé avec succès");
            
        } catch (Exception e) {
            Logger.error("MessageService", "Erreur envoi message", e);
            throw new RuntimeException("Erreur lors de l'envoi du message", e);
        }
    }
    
    public List<Message> getMessagesForUser(int userId) {
        try {
            // TODO: Implement when DAO method exists
            return List.of();
        } catch (Exception e) {
            Logger.error("MessageService", "Erreur récupération messages", e);
            throw new RuntimeException("Erreur lors de la récupération des messages", e);
        }
    }
    
    public List<Message> getConversation(int userId1, int userId2) {
        try {
            // TODO: Implement when DAO method exists  
            return List.of();
        } catch (Exception e) {
            Logger.error("MessageService", "Erreur récupération conversation", e);
            throw new RuntimeException("Erreur lors de la récupération de la conversation", e);
        }
    }
    
    public void markAsRead(int messageId) {
        try {
            messageDAO.markAsRead(messageId);
            Logger.info("MessageService", "Message " + messageId + " marqué comme lu");
        } catch (Exception e) {
            Logger.error("MessageService", "Erreur marquage lecture", e);
            throw new RuntimeException("Erreur lors du marquage comme lu", e);
        }
    }
    
    // Implémentations minimales pour les méthodes de l'interface
    
    @Override
    public List<Message> getReceivedMessages(int userId) throws SQLException {
        return messageDAO.findByReceiver(userId);
    }
    
    @Override
    public List<Message> getSentMessages(int userId) throws SQLException {
        return messageDAO.findBySender(userId);
    }
    
    @Override
    public int getUnreadMessageCount(int userId) throws SQLException {
        return messageDAO.countUnreadByReceiver(userId);
    }
    
    @Override
    public int markAllAsRead(int userId) throws SQLException {
        messageDAO.markAllAsRead(userId);
        return 0; // TODO: retourner le nombre réel
    }
    
    @Override
    public Message sendMessage(Message message) throws SQLException {
        messageDAO.save(message);
        return message;
    }
    
    @Override
    public Message sendPrivateMessage(int senderId, int recipientId, String content) throws SQLException {
        sendMessage(userDAO.findById(senderId).orElse(null), userDAO.findById(recipientId).orElse(null), content);
        return null; // TODO: retourner le message créé
    }
    
    @Override
    public Message sendPublicMessage(int senderId, int courseId, String content) throws SQLException {
        create(courseId, senderId, content);
        return null; // TODO: retourner le message créé
    }
    
    @Override
    public Message sendAnnouncement(int senderId, Integer courseId, String content) throws SQLException {
        return null; // TODO: implémenter
    }
    
    @Override
    public Message createSystemNotification(Integer recipientId, String content, Integer courseId) throws SQLException {
        return null; // TODO: implémenter
    }
    
    @Override
    public Message getMessageById(int messageId) throws SQLException {
        return findById(messageId).orElse(null);
    }
    
    @Override
    public List<Message> getPrivateConversation(int user1Id, int user2Id) throws SQLException {
        return new ArrayList<>(); // TODO: implémenter
    }
    
    @Override
    public List<Message> getCourseMessages(int courseId) throws SQLException {
        return findByCourse(courseId);
    }
    
    @Override
    public List<Message> getUnreadMessages(int userId) throws SQLException {
        return new ArrayList<>(); // TODO: implémenter
    }
    
    @Override
    public List<Message> getRecentAnnouncements(Integer courseId, int limit) throws SQLException {
        return new ArrayList<>(); // TODO: implémenter
    }
    
    @Override
    public boolean markAsRead(int messageId, int userId) throws SQLException {
        messageDAO.markAsRead(messageId);
        return true;
    }
    
    @Override
    public int markMultipleAsRead(List<Integer> messageIds, int userId) throws SQLException {
        return 0; // TODO: implémenter
    }
    
    @Override
    public boolean deleteMessage(int messageId, int userId) throws SQLException {
        messageDAO.delete(messageId);
        return true;
    }
    
    @Override
    public List<Message> searchMessages(int userId, String searchQuery, String messageType) throws SQLException {
        return new ArrayList<>(); // TODO: implémenter
    }
    
    @Override
    public List<Message> getMessagesByDateRange(int userId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) throws SQLException {
        return new ArrayList<>(); // TODO: implémenter
    }
    
    @Override
    public void validateMessageContent(String content, String messageType) {
        // TODO: implémenter validation
    }
    
    @Override
    public boolean canSendMessage(int senderId, int recipientId, String messageType) throws SQLException {
        return true; // TODO: implémenter vérifications
    }
    
    @Override
    public int archiveOldMessages(int olderThanDays) throws SQLException {
        return 0; // TODO: implémenter
    }
    
    // Méthodes de compatibilité avec les servlets existants
    
    public void create(int courseId, int userId, String content) throws SQLException {
        // Cette méthode est utilisée pour les messages de cours
        // Nous allons adapter pour envoyer un message à tous les utilisateurs associés à un cours
        // Pour l'instant, on simule en envoyant un message à l'utilisateur lui-même
        sendMessage(userDAO.findById(userId).orElse(null), userDAO.findById(userId).orElse(null), content);
    }
    
    @Override
    public List<Message> findByCourse(int courseId) throws SQLException {
        // Dans notre nouvelle architecture, nous n'avons pas de messages associés à des cours
        // Retourner une liste vide pour l'instant
        return new ArrayList<>();
    }
    
    @Override
    public Optional<Message> findById(int id) throws SQLException {
        return messageDAO.findById(id);
    }
    
    @Override
    public void delete(int id) throws SQLException {
        messageDAO.delete(id);
    }
} 