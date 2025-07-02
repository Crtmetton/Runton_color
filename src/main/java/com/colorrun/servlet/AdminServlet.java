package com.colorrun.servlet;

import com.colorrun.business.OrganizerRequest;
import com.colorrun.business.User;
import com.colorrun.service.OrganizerRequestService;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.OrganizerRequestServiceImpl;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet d'administration pour gérer les utilisateurs, les demandes d'organisateur et les statistiques.
 */
public class AdminServlet extends HttpServlet {
    
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
            Logger.warn("AdminServlet", "Tentative d'accès non autorisé à l'admin");
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
            Logger.error("AdminServlet", "Erreur de base de données: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erreur de base de données : " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Vérification des droits d'administrateur
        if (!isAdmin(request)) {
            Logger.warn("AdminServlet", "Tentative d'action admin non autorisée");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action requise");
            return;
        }
        
        try {
            switch (action) {
                case "approve-organizer":
                    approveOrganizerRequest(request, response);
                    break;
                case "reject-organizer":
                    rejectOrganizerRequest(request, response);
                    break;
                case "delete-user":
                    deleteUser(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
                    break;
            }
        } catch (SQLException e) {
            Logger.error("AdminServlet", "Erreur lors de l'action " + action + ": " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erreur de base de données : " + e.getMessage());
        }
    }
    
    /**
     * Affiche le tableau de bord administrateur
     */
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        Logger.info("AdminServlet", "Affichage du dashboard admin");
        
        // Récupérer les demandes d'organisateur en attente
        List<OrganizerRequest> pendingRequests = organizerRequestService.getPendingRequests();
        
        // Récupérer tous les utilisateurs
        List<User> allUsers = userService.findAll();
        
        // Récupérer les organisateurs actuels
        List<User> organizers = userService.findByRole("ORGANIZER");
        
        // Statistiques
        long totalUsers = allUsers.size();
        long pendingRequestsCount = pendingRequests.size();
        long organizersCount = organizers.size();
        
        // Ajouter les informations d'authentification pour la navbar
        TokenManager.addTokenToRequest(request);
        
        // Passer les données au template
        request.setAttribute("pendingRequests", pendingRequests);
        request.setAttribute("allUsers", allUsers);
        request.setAttribute("organizers", organizers);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("pendingRequestsCount", pendingRequestsCount);
        request.setAttribute("organizersCount", organizersCount);
        
        Logger.info("AdminServlet", "Dashboard: " + totalUsers + " utilisateurs, " 
                   + pendingRequestsCount + " demandes, " + organizersCount + " organisateurs");
        
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
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
        if (requestIdStr == null || requestIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande requis");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            
            // Récupérer la demande
            Optional<OrganizerRequest> requestOpt = organizerRequestService.findById(requestId);
            if (!requestOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Demande non trouvée");
                return;
            }
            
            OrganizerRequest organizerRequest = requestOpt.get();
            
            // Approuver la demande
            organizerRequestService.approve(requestId);
            
            // Changer le rôle de l'utilisateur
            userService.changeRole(organizerRequest.getRequester().getId(), "ORGANIZER");
            
            Logger.info("AdminServlet", "Demande d'organisateur approuvée pour l'utilisateur " + organizerRequest.getRequester().getId());
            
            // Rediriger vers le dashboard avec un message de succès
            response.sendRedirect(request.getContextPath() + "/admin?success=Demande approuvée avec succès");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande invalide");
        }
    }
    
    /**
     * Rejette une demande d'organisateur
     */
    private void rejectOrganizerRequest(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr == null || requestIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande requis");
            return;
        }
        
        try {
            int requestId = Integer.parseInt(requestIdStr);
            
            // Vérifier que la demande existe
            Optional<OrganizerRequest> requestOpt = organizerRequestService.findById(requestId);
            if (!requestOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Demande non trouvée");
                return;
            }
            
            // Rejeter la demande
            organizerRequestService.reject(requestId);
            
            Logger.info("AdminServlet", "Demande d'organisateur rejetée: " + requestId);
            
            // Rediriger vers le dashboard avec un message de succès
            response.sendRedirect(request.getContextPath() + "/admin?success=Demande rejetée");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de demande invalide");
        }
    }
    
    /**
     * Supprime un utilisateur
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur requis");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            
            // Vérifier que l'utilisateur existe
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
                return;
            }
            
            User user = userOpt.get();
            
            // Empêcher la suppression d'un admin par un autre admin (sécurité)
            if ("ADMIN".equals(user.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Impossible de supprimer un administrateur");
                return;
            }
            
            // Supprimer l'utilisateur
            userService.delete(userId);
            
            Logger.info("AdminServlet", "Utilisateur supprimé: " + user.getEmail() + " (ID: " + userId + ")");
            
            // Rediriger vers le dashboard avec un message de succès
            response.sendRedirect(request.getContextPath() + "/admin?success=Utilisateur supprimé avec succès");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID utilisateur invalide");
        }
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si l'utilisateur connecté est administrateur
     */
    private boolean isAdmin(HttpServletRequest request) {
        UserToken currentToken = TokenManager.getToken(request);
        if (currentToken == null || !currentToken.isAuthenticated()) {
            return false;
        }
        
        return "ADMIN".equals(currentToken.getRole());
    }
    
    /**
     * Obtient le nombre total d'utilisateurs
     */
    private long getTotalUsersCount() throws SQLException {
        try {
            List<User> allUsers = userService.findAll();
            return allUsers.size(); // TODO: Optimiser avec une requête COUNT
        } catch (SQLException e) {
            Logger.error("AdminServlet", "Erreur lors du comptage des utilisateurs: " + e.getMessage());
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
            Logger.error("AdminServlet", "Erreur lors du comptage des demandes: " + e.getMessage());
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
            Logger.error("AdminServlet", "Erreur lors du comptage par rôle: " + e.getMessage());
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