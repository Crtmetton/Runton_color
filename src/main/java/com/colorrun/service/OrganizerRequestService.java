package com.colorrun.service;

import com.colorrun.business.OrganizerRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des demandes de passage au rôle <em>organisateur</em>.
 * <p>
 * Il fournit les opérations suivantes :
 * <ul>
 *   <li>Soumission d'une demande avec motivation</li>
 *   <li>Consultation des demandes en attente ou existantes</li>
 *   <li>Validation (approbation) ou rejet par un administrateur</li>
 *   <li>Méthodes de compatibilité « legacy » utilisées par les servlets</li>
 * </ul>
 * Toutes les méthodes déclarent {@link java.sql.SQLException} afin de laisser
 * la couche supérieure gérer proprement les erreurs d'accès aux données.
 * </p>
 */
public interface OrganizerRequestService {
    
    /**
     * Soumet une demande pour devenir organisateur.
     *
     * @param userId     identifiant de l'utilisateur demandeur
     * @param motivation texte de motivation
     */
    void submitRequest(int userId, String motivation) throws SQLException;
    
    List<OrganizerRequest> getPendingRequests() throws SQLException;
    
    OrganizerRequest getRequestById(int id) throws SQLException;
    
    Optional<OrganizerRequest> getUserRequest(int userId) throws SQLException;
    
    void approveRequest(int requestId) throws SQLException;
    
    void rejectRequest(int requestId) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    void create(int userId, String motivation) throws SQLException;
    
    List<OrganizerRequest> findPending() throws SQLException;
    
    Optional<OrganizerRequest> findById(int id) throws SQLException;
    
    void approve(int requestId) throws SQLException;
    
    void reject(int requestId) throws SQLException;
    
    boolean hasRequestForUser(int userId) throws SQLException;
} 