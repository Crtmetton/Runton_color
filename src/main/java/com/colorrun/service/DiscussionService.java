package com.colorrun.service;

import com.colorrun.business.Discussion;
import java.util.List;
import java.util.Optional;

public interface DiscussionService {
    
    /**
     * Crée un nouveau message de discussion pour une course
     */
    void create(int courseId, int userId, String content);
    
    /**
     * Récupère tous les messages d'une course, triés par date (plus anciens en haut)
     */
    List<Discussion> findByCourse(int courseId);
    
    /**
     * Récupère un message par son ID
     */
    Optional<Discussion> findById(int id);
    
    /**
     * Supprime un message de discussion
     */
    void delete(int id);
    
    /**
     * Met à jour le contenu d'un message
     */
    void updateContent(int id, String newContent);
    
    /**
     * Compte le nombre de messages dans une course
     */
    int countByCourse(int courseId);
    
    /**
     * Récupère tous les messages d'un utilisateur
     */
    List<Discussion> findByUser(int userId);
} 