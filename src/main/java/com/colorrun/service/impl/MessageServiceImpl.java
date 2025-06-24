package com.colorrun.service.impl;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import com.colorrun.dao.MessageDAO;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.MessageService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageServiceImpl implements MessageService {
    
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    
    public MessageServiceImpl() {
        this.messageDAO = new MessageDAO();
        this.userDAO = new UserDAO();
    }
    
    @Override
    public void sendMessage(int senderId, int receiverId, String content) throws SQLException {
        // Vérifier si l'expéditeur existe
        Optional<User> senderOpt = userDAO.findById(senderId);
        if (!senderOpt.isPresent()) {
            throw new SQLException("Sender not found");
        }
        
        // Vérifier si le destinataire existe
        Optional<User> receiverOpt = userDAO.findById(receiverId);
        if (!receiverOpt.isPresent()) {
            throw new SQLException("Receiver not found");
        }
        
        // Créer le message
        Message message = new Message();
        message.setSender(senderOpt.get());
        message.setReceiver(receiverOpt.get());
        message.setContent(content);
        message.setDate(LocalDateTime.now());
        message.setRead(false);
        
        // Enregistrer le message
        messageDAO.save(message);
    }
    
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
    public void markAsRead(int messageId) throws SQLException {
        messageDAO.markAsRead(messageId);
    }
    
    @Override
    public void markAllAsRead(int userId) throws SQLException {
        messageDAO.markAllAsRead(userId);
    }
    
    @Override
    public void deleteMessage(int messageId) throws SQLException {
        messageDAO.delete(messageId);
    }
    
    // Méthodes de compatibilité avec les servlets existants
    
    @Override
    public void create(int courseId, int userId, String content) throws SQLException {
        // Cette méthode est utilisée pour les messages de cours
        // Nous allons adapter pour envoyer un message à tous les utilisateurs associés à un cours
        // Pour l'instant, on simule en envoyant un message à l'utilisateur lui-même
        sendMessage(userId, userId, content);
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
        deleteMessage(id);
    }
} 