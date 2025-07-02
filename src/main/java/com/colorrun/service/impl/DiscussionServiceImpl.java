package com.colorrun.service.impl;

import com.colorrun.business.Discussion;
import com.colorrun.dao.DiscussionDAO;
import com.colorrun.service.DiscussionService;
import com.colorrun.util.Logger;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DiscussionServiceImpl implements DiscussionService {
    
    private DiscussionDAO discussionDAO;
    
    public DiscussionServiceImpl() {
        this.discussionDAO = new DiscussionDAO();
    }
    
    @Override
    public void create(int courseId, int userId, String content) {
        Logger.info("DiscussionService", "Création d'un nouveau message de discussion pour la course " + courseId);
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide");
        }
        
        try {
            Discussion discussion = new Discussion(courseId, content.trim(), userId);
            discussion.setDate(LocalDateTime.now());
            discussionDAO.save(discussion);
            
            Logger.info("DiscussionService", "Message créé avec succès pour la course " + courseId);
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors de la création du message: " + e.getMessage());
            throw new RuntimeException("Impossible de créer le message", e);
        }
    }
    
    @Override
    public List<Discussion> findByCourse(int courseId) {
        Logger.debug("DiscussionService", "Récupération des messages pour la course " + courseId);
        
        try {
            List<Discussion> discussions = discussionDAO.findByCourse(courseId);
            Logger.debug("DiscussionService", discussions.size() + " message(s) trouvé(s) pour la course " + courseId);
            return discussions;
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors de la récupération des messages: " + e.getMessage());
            throw new RuntimeException("Impossible de récupérer les messages", e);
        }
    }
    
    @Override
    public Optional<Discussion> findById(int id) {
        Logger.debug("DiscussionService", "Recherche du message avec ID " + id);
        
        try {
            return discussionDAO.findById(id);
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors de la recherche du message " + id + ": " + e.getMessage());
            throw new RuntimeException("Impossible de trouver le message", e);
        }
    }
    
    @Override
    public void delete(int id) {
        Logger.info("DiscussionService", "Suppression du message " + id);
        
        try {
            discussionDAO.delete(id);
            Logger.info("DiscussionService", "Message " + id + " supprimé avec succès");
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors de la suppression du message " + id + ": " + e.getMessage());
            throw new RuntimeException("Impossible de supprimer le message", e);
        }
    }
    
    @Override
    public void updateContent(int id, String newContent) {
        Logger.info("DiscussionService", "Mise à jour du contenu du message " + id);
        
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau contenu ne peut pas être vide");
        }
        
        try {
            discussionDAO.updateContent(id, newContent.trim());
            Logger.info("DiscussionService", "Contenu du message " + id + " mis à jour avec succès");
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors de la mise à jour du message " + id + ": " + e.getMessage());
            throw new RuntimeException("Impossible de mettre à jour le message", e);
        }
    }
    
    @Override
    public int countByCourse(int courseId) {
        Logger.debug("DiscussionService", "Comptage des messages pour la course " + courseId);
        
        try {
            int count = discussionDAO.countByCourse(courseId);
            Logger.debug("DiscussionService", count + " message(s) dans la course " + courseId);
            return count;
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors du comptage des messages: " + e.getMessage());
            throw new RuntimeException("Impossible de compter les messages", e);
        }
    }
    
    @Override
    public List<Discussion> findByUser(int userId) {
        Logger.debug("DiscussionService", "Récupération des messages de l'utilisateur " + userId);
        
        try {
            List<Discussion> discussions = discussionDAO.findByUser(userId);
            Logger.debug("DiscussionService", discussions.size() + " message(s) trouvé(s) pour l'utilisateur " + userId);
            return discussions;
        } catch (SQLException e) {
            Logger.error("DiscussionService", "Erreur lors de la récupération des messages de l'utilisateur: " + e.getMessage());
            throw new RuntimeException("Impossible de récupérer les messages de l'utilisateur", e);
        }
    }
} 