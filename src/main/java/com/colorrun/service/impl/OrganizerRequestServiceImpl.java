package com.colorrun.service.impl;

import com.colorrun.business.OrganizerRequest;
import com.colorrun.business.User;
import com.colorrun.dao.OrganizerRequestDAO;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.OrganizerRequestService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrganizerRequestServiceImpl implements OrganizerRequestService {
    
    private OrganizerRequestDAO organizerRequestDAO;
    private UserDAO userDAO;
    
    public OrganizerRequestServiceImpl() {
        this.organizerRequestDAO = new OrganizerRequestDAO();
        this.userDAO = new UserDAO();
    }
    
    @Override
    public void submitRequest(int userId, String motivation) throws SQLException {
        // Vérifier si l'utilisateur existe
        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            throw new SQLException("User not found");
        }
        
        // Vérifier si l'utilisateur a déjà fait une demande
        Optional<OrganizerRequest> existingRequestOpt = organizerRequestDAO.findByUser(userId);
        if (existingRequestOpt.isPresent()) {
            throw new SQLException("User has already submitted a request");
        }
        
        // Créer une nouvelle demande
        OrganizerRequest request = new OrganizerRequest();
        request.setUser(userOpt.get());
        request.setMotivation(motivation);
        request.setDate(LocalDateTime.now());
        request.setStatus("PENDING");
        
        // Enregistrer la demande
        organizerRequestDAO.save(request);
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
        User user = request.getUser();
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
    public boolean hasRequestForUser(int userId) throws SQLException {
        Optional<OrganizerRequest> requestOpt = getUserRequest(userId);
        return requestOpt.isPresent() && 
               (requestOpt.get().getStatus().equals("PENDING") || 
                requestOpt.get().getStatus().equals("APPROVED"));
    }
} 