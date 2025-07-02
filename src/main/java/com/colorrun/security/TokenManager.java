package com.colorrun.security;

import com.colorrun.business.User;
import com.colorrun.util.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Gestionnaire centralisé des tokens utilisateur
 * Gère la création, validation et destruction des tokens
 */
public class TokenManager {
    
    private static final String TOKEN_SESSION_KEY = "USER_TOKEN";
    
    /**
     * Crée un nouveau token pour l'utilisateur et le stocke en session
     */
    public static UserToken createToken(User user, HttpSession session) {
        Logger.step("TokenManager", "Création token pour " + user.getEmail());
        
        UserToken token = new UserToken(user);
        session.setAttribute(TOKEN_SESSION_KEY, token);
        
        Logger.success("TokenManager", "Token créé: " + token.getTokenId().substring(0, 8) + 
                       " pour " + token.getFullName() + " (" + token.getRole() + ")");
        
        return token;
    }
    
    /**
     * Récupère le token depuis la session
     */
    public static UserToken getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        
        UserToken token = (UserToken) session.getAttribute(TOKEN_SESSION_KEY);
        
        if (token != null) {
            // Vérifier si le token est toujours valide
            if (token.isExpired()) {
                Logger.warn("TokenManager", "Token expiré pour " + token.getEmail());
                destroyToken(session);
                return null;
            }
            
            // Mettre à jour l'activité
            token.updateActivity();
            Logger.debug("TokenManager", "Token valide pour " + token.getEmail() + " (" + token.getRole() + ")");
        }
        
        return token;
    }
    
    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        UserToken token = getToken(request);
        boolean authenticated = token != null && token.isAuthenticated();
        
        if (!authenticated) {
            Logger.debug("TokenManager", "Utilisateur non authentifié");
        }
        
        return authenticated;
    }
    
    /**
     * Vérifie si l'utilisateur a le rôle administrateur
     */
    public static boolean isAdmin(HttpServletRequest request) {
        UserToken token = getToken(request);
        boolean isAdmin = token != null && token.isAdmin();
        
        Logger.debug("TokenManager", "Vérification admin: " + isAdmin);
        return isAdmin;
    }
    
    /**
     * Vérifie si l'utilisateur est organisateur (ou admin)
     */
    public static boolean isOrganizer(HttpServletRequest request) {
        UserToken token = getToken(request);
        boolean isOrganizer = token != null && token.isOrganizer();
        
        Logger.debug("TokenManager", "Vérification organisateur: " + isOrganizer);
        return isOrganizer;
    }
    
    /**
     * Vérifie si l'utilisateur est un simple utilisateur
     */
    public static boolean isUser(HttpServletRequest request) {
        UserToken token = getToken(request);
        boolean isUser = token != null && token.isUser();
        
        Logger.debug("TokenManager", "Vérification utilisateur: " + isUser);
        return isUser;
    }
    
    /**
     * Détruit le token (déconnexion)
     */
    public static void destroyToken(HttpSession session) {
        UserToken token = (UserToken) session.getAttribute(TOKEN_SESSION_KEY);
        
        if (token != null) {
            Logger.step("TokenManager", "Destruction token pour " + token.getEmail());
            token.invalidate();
            session.removeAttribute(TOKEN_SESSION_KEY);
            Logger.success("TokenManager", "Token détruit avec succès");
        }
    }
    
    /**
     * Ajoute les informations du token aux attributs de la requête
     * Pour utilisation dans les JSP/vues
     */
    public static void addTokenToRequest(HttpServletRequest request) {
        UserToken token = getToken(request);
        
        if (token != null && token.isAuthenticated()) {
            request.setAttribute("userToken", token);
            request.setAttribute("isAuthenticated", true);
            request.setAttribute("isAdmin", token.isAdmin());
            request.setAttribute("isOrganizer", token.isOrganizer());
            request.setAttribute("isUser", token.isUser());
            request.setAttribute("currentUser", token);
            request.setAttribute("userName", token.getFullName());
            request.setAttribute("userRole", token.getRole()); // Rôle technique (USER, ORGANIZER, ADMIN)
            request.setAttribute("userRoleDescription", token.getRoleDescription()); // Description française
            
            Logger.debug("TokenManager", "Informations token ajoutées à la requête pour " + token.getEmail());
        } else {
            request.setAttribute("userToken", null);
            request.setAttribute("isAuthenticated", false);
            request.setAttribute("isAdmin", false);
            request.setAttribute("isOrganizer", false);
            request.setAttribute("isUser", false);
            request.setAttribute("currentUser", null);
            request.setAttribute("userName", "");
            request.setAttribute("userRole", "");
            request.setAttribute("userRoleDescription", "");
            
            Logger.debug("TokenManager", "Aucune authentification - attributs vides ajoutés");
        }
    }
    
    /**
     * Redirige vers la page de connexion si non authentifié
     */
    public static boolean requireAuthentication(HttpServletRequest request, 
                                              HttpServletResponse response, 
                                              String redirectPath) throws Exception {
        if (!isAuthenticated(request)) {
            Logger.warn("TokenManager", "Accès refusé - authentification requise");
            response.sendRedirect(request.getContextPath() + redirectPath);
            return false;
        }
        return true;
    }
    
    /**
     * Vérifie les permissions pour organisateur
     */
    public static boolean requireOrganizerRole(HttpServletRequest request, 
                                             HttpServletResponse response) throws Exception {
        if (!isOrganizer(request)) {
            Logger.error("TokenManager", "Accès refusé - rôle organisateur requis");
            request.setAttribute("error", "Vous devez être organisateur pour accéder à cette page.");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            return false;
        }
        return true;
    }
    
    /**
     * Vérifie les permissions pour administrateur
     */
    public static boolean requireAdminRole(HttpServletRequest request, 
                                         HttpServletResponse response) throws Exception {
        if (!isAdmin(request)) {
            Logger.error("TokenManager", "Accès refusé - rôle administrateur requis");
            request.setAttribute("error", "Vous devez être administrateur pour accéder à cette page.");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            return false;
        }
        return true;
    }
} 