package com.colorrun.service.impl;

import com.colorrun.business.OrganizerRequest;
import com.colorrun.business.User;
import com.colorrun.dao.OrganizerRequestDAO;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.OrganizerRequestService;
import com.colorrun.util.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation concrète de {@link com.colorrun.service.OrganizerRequestService}.
 * <p>
 * Cette classe orchestre la persistance via {@link com.colorrun.dao.OrganizerRequestDAO}
 * et {@link com.colorrun.dao.UserDAO}, ainsi que les validations de base
 * (existence d'utilisateur, changements de rôle, etc.).
 * </p>
 */
public class OrganizerRequestServiceImpl implements OrganizerRequestService {
    
    private OrganizerRequestDAO organizerRequestDAO;
    private UserDAO userDAO;
    
    public OrganizerRequestServiceImpl() {
        this.organizerRequestDAO = new OrganizerRequestDAO();
        this.userDAO = new UserDAO();
    }
    
    @Override
    public void submitRequest(int userId, String reason) {
        try {
            Logger.info("OrganizerRequestService", "Soumission demande organisateur pour utilisateur " + userId);
            
            User user = userDAO.findById(userId).orElseThrow();
            
            OrganizerRequest request = new OrganizerRequest();
            request.setRequester(user);
            request.setReason(reason);
            request.setSubmissionDate(LocalDateTime.now());
            request.setStatus("PENDING");
            
            organizerRequestDAO.save(request);
            Logger.success("OrganizerRequestService", "Demande soumise avec succès");
            
        } catch (Exception e) {
            Logger.error("OrganizerRequestService", "Erreur soumission demande", e);
            throw new RuntimeException("Erreur lors de la soumission de la demande", e);
        }
    }
    
    @Override
    public List<OrganizerRequest> getPendingRequests() throws SQLException {
        return organizerRequestDAO.findByStatus("PENDING");
    }
    
    @Override
    public OrganizerRequest getRequestById(int id) throws SQLException {
        Optional<OrganizerRequest> requestOpt = organizerRequestDAO.findById(id);
        if (!requestOpt.isPresent()) {
            throw new SQLException("Request not found");
        }
        return requestOpt.get();
    }
    
    @Override
    public Optional<OrganizerRequest> getUserRequest(int userId) throws SQLException {
        return organizerRequestDAO.findByUser(userId);
    }
    
    @Override
    public void approveRequest(int requestId) throws SQLException {
        Optional<OrganizerRequest> requestOpt = organizerRequestDAO.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new SQLException("Request not found");
        }
        
        OrganizerRequest request = requestOpt.get();
        
        // Mettre à jour le statut de la demande
        request.setStatus("APPROVED");
        organizerRequestDAO.update(request);
        
        // Mettre à jour le rôle de l'utilisateur
        User user = request.getRequester();
        user.setRole("ORGANIZER");
        userDAO.update(user);
    }
    
    @Override
    public void rejectRequest(int requestId) throws SQLException {
        Optional<OrganizerRequest> requestOpt = organizerRequestDAO.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new SQLException("Request not found");
        }
        
        OrganizerRequest request = requestOpt.get();
        
        // Mettre à jour le statut de la demande
        request.setStatus("REJECTED");
        organizerRequestDAO.update(request);
    }
    
    // Méthodes de compatibilité avec les servlets existants
    
    @Override
    public void create(int userId, String motivation) throws SQLException {
        submitRequest(userId, motivation);
    }
    
    @Override
    public List<OrganizerRequest> findPending() throws SQLException {
        return getPendingRequests();
    }
    
    @Override
    public Optional<OrganizerRequest> findById(int id) throws SQLException {
        try {
            OrganizerRequest request = getRequestById(id);
            return Optional.of(request);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public void approve(int requestId) throws SQLException {
        approveRequest(requestId);
    }
    
    @Override
    public void reject(int requestId) throws SQLException {
        rejectRequest(requestId);
    }
    
    @Override
    public boolean hasRequestForUser(int userId) {
        try {
            // TODO: Implement when DAO method exists
            return false;
        } catch (Exception e) {
            Logger.error("OrganizerRequestService", "Erreur vérification demande", e);
            return false;
        }
    }
} 