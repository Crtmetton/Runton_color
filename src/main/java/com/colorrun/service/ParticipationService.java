package com.colorrun.service;

import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des inscriptions (participations) des utilisateurs
 * aux courses Color Run.
 * <p>
 * Responsabilités :
 * <ul>
 *   <li>Inscription d'un utilisateur à une course avec vérification de capacité</li>
 *   <li>Consultation, annulation de participation</li>
 *   <li>Génération de PDF de dossard (méthode avancée)</li>
 *   <li>Méthodes d'aide pour les servlets legacy (alias create/find...)</li>
 * </ul>
 * Les méthodes lèvent {@link java.sql.SQLException} lorsque des opérations
 * d'E/S sur la base échouent.
 * </p>
 */
public interface ParticipationService {
    
    /**
     * Inscrit un utilisateur à une course si des places sont disponibles.
     *
     * @param userId   identifiant de l'utilisateur
     * @param courseId identifiant de la course
     */
    void registerParticipation(int userId, int courseId) throws SQLException;
    
    List<Participation> getUserParticipations(int userId) throws SQLException;
    
    List<Participation> getCourseParticipations(int courseId) throws SQLException;
    
    boolean isUserRegistered(int userId, int courseId) throws SQLException;
    
    void cancelParticipation(int userId, int courseId) throws SQLException;
    
    List<Course> getUserCourses(int userId) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    int register(int userId, int courseId) throws SQLException;
    
    Optional<Participation> findById(int id) throws SQLException;
    
    List<Participation> findByUser(int userId) throws SQLException;
    
    List<Participation> findByCourse(int courseId) throws SQLException;
    
    List<Course> findCoursesByUser(int userId) throws SQLException;
    
    void cancel(int participationId) throws SQLException;
    
    byte[] generateBibPdf(int participationId) throws SQLException;
} 