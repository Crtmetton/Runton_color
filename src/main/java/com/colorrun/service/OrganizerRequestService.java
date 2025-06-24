package com.colorrun.service;

import com.colorrun.business.OrganizerRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrganizerRequestService {
    
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