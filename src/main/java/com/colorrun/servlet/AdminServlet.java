package com.colorrun.servlet;

import com.colorrun.business.OrganizerRequest;
import com.colorrun.business.User;
import com.colorrun.service.OrganizerRequestService;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.OrganizerRequestServiceImpl;
import com.colorrun.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet d'administration pour gérer les utilisateurs, les demandes d'organisateur et les statistiques.
 */
public class AdminServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());
    
    private UserService userService;
    private OrganizerRequestService organizerRequestService;
    
    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
        this.organizerRequestService = new OrganizerRequestServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérification des droits d'administrateur
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        
        try {
            switch (pathInfo) {
                case "/":
                case "/dashboard":
                    showDashboard(request, response);
                    break;
                case "/organizer-requests":
                    showOrganizerRequests(request, response);
                    break;
                case "/users":
                    showUsers(request, response);
                    break;
                case "/statistics":
                    showStatistics(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur de base de données dans AdminServlet", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erreur de base de données : " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Vérification des droits d'administrateur
        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            switch (pathInfo) {
                case "/approve-organizer":
                    approveOrganizerRequest(request, response);
                    break;
                case "/reject-organizer":
                    rejectOrganizerRequest(request, response);
                    break;
                case "/change-user-role":
                    changeUserRole(request, response);
                    break;
                case "/reset-password":
                    resetUserPassword(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur de base de données dans AdminServlet POST", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erreur de base de données : " + e.getMessage());
        }
    }
    
    /**
     * Affiche le tableau de bord administrateur
     */
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        // Statistiques de base
        long totalUsers = getTotalUsersCount();
        long pendingRequests = getPendingRequestsCount();
        long totalOrganizers = getOrganizersCount();
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("pendingRequests", pendingRequests);
        request.setAttribute("totalOrganizers", totalOrganizers);
        
        // Demandes récentes
        List<OrganizerRequest> recentRequests = organizerRequestService.getPendingRequests();
        request.setAttribute("recentRequests", recentRequests);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.html").forward(request, response);
    }
    
    /**
     * Affiche la liste des demandes d'organisateur
     */
    private void showOrganizerRequests(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String status = request.getParameter("status");
        List<OrganizerRequest> requests;
        
        if ("all".equals(status)) {
            requests = organizerRequestService.findPending(); // TODO: Implémenter findAll()
        } else {
            requests = organizerRequestService.getPendingRequests();
        }
        
        request.setAttribute("requests", requests);
        request.setAttribute("currentStatus", status != null ? status : "pending");
        
        request.getRequestDispatcher("/WEB-INF/views/admin/organizer-requests.html").forward(request, response);
    }
    
    /**
     * Affiche la liste des utilisateurs
     */
    private void showUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String role = request.getParameter("role");
        String search = request.getParameter("search");
        
        List<User> users = getFilteredUsers(role, search);
        
        request.setAttribute("users", users);
        request.setAttribute("currentRole", role);
        request.setAttribute("currentSearch", search);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/users.html").forward(request, response);
    }
    
    /**
     * Affiche les statistiques
     */
    private void showStatistics(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        // Statistiques détaillées
        long totalUsers = getTotalUsersCount();
        long participants = getUsersByRole("PARTICIPANT");
        long organizers = getUsersByRole("ORGANIZER");
        long admins = getUsersByRole("ADMIN");
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("participants", participants);
        request.setAttribute("organizers", organizers);
        request.setAttribute("admins", admins);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/statistics.html").forward(request, response);
    }
    
    /**
     * Approuve une demande d'organisateur
     */
    private void approveOrganizerRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande manquant");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            organizerRequestService.approveRequest(requestId);
            
            request.getSession().setAttribute("successMessage", "Demande approuvée avec succès !");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande invalide");
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/organizer-requests");
    }
    
    /**
     * Rejette une demande d'organisateur
     */
    private void rejectOrganizerRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande manquant");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            organizerRequestService.rejectRequest(requestId);
            
            request.getSession().setAttribute("successMessage", "Demande rejetée.");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande invalide");
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/organizer-requests");
    }
    
    /**
     * Change le rôle d'un utilisateur
     */
    private void changeUserRole(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String userIdStr = request.getParameter("userId");
        String newRole = request.getParameter("role");
        
        if (userIdStr == null || newRole == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            java.util.Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setRole(newRole);
                userService.update(user);
                
                request.getSession().setAttribute("successMessage", 
                    "Rôle de l'utilisateur modifié avec succès !");
            } else {
                request.getSession().setAttribute("errorMessage", "Utilisateur introuvable.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide");
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    /**
     * Réinitialise le mot de passe d'un utilisateur
     */
    private void resetUserPassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String userIdStr = request.getParameter("userId");
        String newPassword = request.getParameter("newPassword");
        
        if (userIdStr == null || newPassword == null || newPassword.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            java.util.Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // TODO: Hasher le mot de passe avec BCrypt avant de le stocker
                user.setPasswordHash(newPassword); // Note: Doit être hashé en production
                userService.update(user);
                
                request.getSession().setAttribute("successMessage", 
                    "Mot de passe réinitialisé avec succès !");
            } else {
                request.getSession().setAttribute("errorMessage", "Utilisateur introuvable.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide");
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si l'utilisateur connecté est administrateur
     */
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    /**
     * Obtient le nombre total d'utilisateurs
     */
    private long getTotalUsersCount() throws SQLException {
        try {
            List<User> allUsers = userService.findAll();
            return allUsers.size(); // TODO: Optimiser avec une requête COUNT
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage des utilisateurs", e);
            return 0;
        }
    }
    
    /**
     * Obtient le nombre de demandes en attente
     */
    private long getPendingRequestsCount() throws SQLException {
        try {
            List<OrganizerRequest> pendingRequests = organizerRequestService.getPendingRequests();
            return pendingRequests.size(); // TODO: Optimiser avec une requête COUNT
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage des demandes", e);
            return 0;
        }
    }
    
    /**
     * Obtient le nombre d'organisateurs
     */
    private long getOrganizersCount() throws SQLException {
        return getUsersByRole("ORGANIZER");
    }
    
    /**
     * Obtient le nombre d'utilisateurs par rôle
     */
    private long getUsersByRole(String role) throws SQLException {
        try {
            List<User> allUsers = userService.findAll();
            return allUsers.stream()
                    .filter(user -> role.equals(user.getRole()))
                    .count();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du comptage par rôle", e);
            return 0;
        }
    }
    
    /**
     * Obtient les utilisateurs filtrés
     */
    private List<User> getFilteredUsers(String role, String search) throws SQLException {
        List<User> allUsers = userService.findAll();
        
        return allUsers.stream()
                .filter(user -> role == null || role.isEmpty() || role.equals(user.getRole()))
                .filter(user -> search == null || search.isEmpty() || 
                    user.getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                    user.getLastName().toLowerCase().contains(search.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(search.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
} 