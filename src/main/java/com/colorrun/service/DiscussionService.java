package com.colorrun.service;

import com.colorrun.business.Discussion;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des messages de discussion liés aux courses.
 * <p>
 * Fournit des opérations de création, lecture, mise à jour et suppression
 * (CRUD) ainsi que des méthodes utilitaires comme le comptage des messages
 * pour une course donnée.
 * </p>
 */
public interface DiscussionService {
    
    /**
     * Crée un nouveau message de discussion pour une course.
     *
     * @param courseId identifiant de la course
     * @param userId   identifiant de l'expéditeur
     * @param content  contenu textuel du message
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