package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import com.colorrun.service.OrganizerRequestService;
import com.colorrun.service.impl.OrganizerRequestServiceImpl;
import com.colorrun.business.User;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;

@WebServlet(name = "OrganizerRequestServlet", urlPatterns = {"/organizer/request"})
public class OrganizerRequestServlet extends HttpServlet {
    
    private OrganizerRequestService organizerRequestService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.organizerRequestService = new OrganizerRequestServiceImpl();
        Logger.info("OrganizerRequestServlet", "✅ Service demandes organisateur initialisé");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Logger.separator("ACCÈS DEVENIR ORGANISATEUR");
        Logger.step("OrganizerRequestServlet", "🔄 Chargement page devenir organisateur");
        
        // Vérifier l'authentification
        if (!TokenManager.isAuthenticated(request)) {
            Logger.warn("OrganizerRequestServlet", "Utilisateur non connecté - redirection");
            request.setAttribute("error", "Vous devez être connecté pour faire une demande d'organisateur.");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            return;
        }
        
        // Récupération du token utilisateur
        UserToken userToken = (UserToken) request.getAttribute("userToken");
        if (userToken == null) {
            Logger.error("OrganizerRequestServlet", "Token utilisateur non trouvé");
            request.setAttribute("error", "Erreur d'authentification.");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            return;
        }
        
        int userId = userToken.getUserId();
        Logger.debug("OrganizerRequestServlet", "ID utilisateur récupéré: " + userId);
        
        // Vérifier si l'utilisateur a déjà une demande en cours
        try {
            boolean hasRequest = organizerRequestService.hasRequestForUser(userId);
            request.setAttribute("hasExistingRequest", hasRequest);
            
            if (hasRequest) {
                Logger.info("OrganizerRequestServlet", "Demande existante trouvée pour l'utilisateur " + userId);
                request.setAttribute("message", "Vous avez déjà une demande d'organisateur en cours de traitement.");
            }
            
        } catch (Exception e) {
            Logger.error("OrganizerRequestServlet", "Erreur lors de la vérification: " + e.getMessage());
            request.setAttribute("error", "Erreur lors de la vérification de votre demande.");
        }
        
        Logger.success("OrganizerRequestServlet", "Page devenir organisateur chargée");
        request.getRequestDispatcher("/WEB-INF/views/devenir-organisateur.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Logger.separator("SOUMISSION DEMANDE ORGANISATEUR");
        Logger.step("OrganizerRequestServlet", "🔄 Traitement demande organisateur");
        
        request.setCharacterEncoding("UTF-8");
        
        // Vérifier l'authentification
        if (!TokenManager.isAuthenticated(request)) {
            Logger.warn("OrganizerRequestServlet", "Utilisateur non connecté - redirection");
            request.setAttribute("error", "Vous devez être connecté pour faire une demande d'organisateur.");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            return;
        }
        
        // Récupération du token utilisateur
        UserToken userToken = (UserToken) request.getAttribute("userToken");
        if (userToken == null) {
            Logger.error("OrganizerRequestServlet", "Token utilisateur non trouvé");
            request.setAttribute("error", "Erreur d'authentification.");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            return;
        }
        
        int userId = userToken.getUserId();
        String reason = request.getParameter("motivation");
        
        Logger.debug("OrganizerRequestServlet", "Demande de: " + userToken.getFullName());
        Logger.debug("OrganizerRequestServlet", "Motivation: " + reason);
        
        if (reason == null || reason.trim().isEmpty()) {
            Logger.warn("OrganizerRequestServlet", "Motivation vide");
            request.setAttribute("error", "La motivation est obligatoire.");
            request.getRequestDispatcher("/WEB-INF/views/devenir-organisateur.jsp").forward(request, response);
            return;
        }
        
        try {
            // Vérifier si une demande existe déjà
            if (organizerRequestService.hasRequestForUser(userId)) {
                Logger.warn("OrganizerRequestServlet", "Demande déjà existante pour " + userId);
                request.setAttribute("error", "Vous avez déjà une demande en cours de traitement.");
                request.getRequestDispatcher("/WEB-INF/views/devenir-organisateur.jsp").forward(request, response);
                return;
            }
            
            // Créer la demande
            organizerRequestService.submitRequest(userId, reason);
            Logger.success("OrganizerRequestServlet", "Demande créée avec succès pour " + userToken.getFullName());
            request.setAttribute("success", "Votre demande d'organisateur a été envoyée avec succès ! Elle sera traitée par un administrateur.");
            
        } catch (Exception e) {
            Logger.error("OrganizerRequestServlet", "Erreur: " + e.getMessage());
            request.setAttribute("error", "Erreur lors du traitement de votre demande: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/devenir-organisateur.jsp").forward(request, response);
    }
}
