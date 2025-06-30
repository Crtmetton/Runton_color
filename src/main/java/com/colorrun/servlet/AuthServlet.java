package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.business.User;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;

/**
 * Servlet de gestion de l'authentification des utilisateurs.
 * 
 * Cette servlet traite les demandes de connexion (login) des utilisateurs.
 * Elle vérifie les identifiants fournis, crée une session utilisateur en cas de succès,
 * et redirige vers la page appropriée selon le résultat de l'authentification.
 * 
 * <p><strong>Fonctionnalités :</strong></p>
 * <ul>
 *   <li>Traitement des requêtes POST de connexion</li>
 *   <li>Validation des identifiants (email/mot de passe)</li>
 *   <li>Gestion des sessions utilisateur</li>
 *   <li>Redirection selon l'état de l'authentification</li>
 *   <li>Support de l'encodage UTF-8 pour les caractères spéciaux</li>
 * </ul>
 * 
 * <p><strong>Paramètres attendus :</strong></p>
 * <ul>
 *   <li><code>email</code> : Adresse email de l'utilisateur</li>
 *   <li><code>password</code> : Mot de passe en clair</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see UserService Pour la logique d'authentification
 * @see User Pour le modèle utilisateur
 */
public class AuthServlet extends HttpServlet {
    
    /** Service de gestion des utilisateurs pour l'authentification */
    private final UserService userService;
    
    /**
     * Constructeur par défaut.
     * Initialise le service utilisateur pour les opérations d'authentification.
     */
    public AuthServlet() {
        this.userService = new UserServiceImpl();
    }
    
    /**
     * Traite les requêtes POST de connexion utilisateur.
     * 
     * Cette méthode récupère les identifiants fournis via le formulaire de connexion,
     * tente de les authentifier via le service utilisateur, et gère la création
     * de session en cas de succès ou affiche les erreurs en cas d'échec.
     * 
     * <p><strong>Flux de traitement :</strong></p>
     * <ol>
     *   <li>Configuration de l'encodage UTF-8</li>
     *   <li>Récupération des paramètres email et password</li>
     *   <li>Validation de la présence des paramètres</li>
     *   <li>Tentative d'authentification via UserService</li>
     *   <li>Création de session si succès, sinon affichage d'erreur</li>
     *   <li>Redirection appropriée</li>
     * </ol>
     * 
     * @param req La requête HTTP contenant les identifiants
     * @param resp La réponse HTTP pour la redirection
     * @throws ServletException Si erreur lors du traitement de la servlet
     * @throws IOException Si erreur lors des opérations d'E/S
     * 
     * @example
     * POST /login
     * Content-Type: application/x-www-form-urlencoded
     * 
     * email=jean@example.com&password=motdepasse123
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Configuration de l'encodage pour supporter les caractères spéciaux français
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        
        // Récupération des paramètres de connexion depuis le formulaire
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        Logger.debug("AuthServlet", "Tentative de connexion pour : " + email);
        Logger.debug("AuthServlet", "Mot de passe reçu (masqué) : " + (password != null ? "[REÇU]" : "[VIDE]"));
        
        // Validation de la présence des paramètres obligatoires
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            Logger.warn("AuthServlet", "Champs email ou mot de passe manquants");
            req.setAttribute("error", "Veuillez remplir tous les champs");
            req.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(req, resp);
            return;
        }
        
        try {
            // Tentative d'authentification via le service utilisateur
            User user = userService.authenticate(email.trim(), password);
            Logger.debug("AuthServlet", "Authentification réussie pour : " + email);
            
            // Authentification réussie - création du token utilisateur
            HttpSession session = req.getSession();
            
            // Créer et stocker le token utilisateur
            UserToken token = TokenManager.createToken(user, session);
            Logger.info("AuthServlet", "Token créé pour " + user.getFirstName() + " " + user.getLastName() + 
                       " - Rôle: " + token.getRoleDescription());
            
            // Garder l'objet user pour compatibilité (temporaire)
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            
            // Détermination de la page de redirection selon le rôle utilisateur
            String redirectUrl = determineRedirectUrl(user.getRole());
            
            // Message de succès personnalisé avec rôle
            session.setAttribute("success", "Bienvenue " + user.getFirstName() + " (" + token.getRoleDescription() + ") !");
            
            // Redirection vers la page appropriée
            resp.sendRedirect(req.getContextPath() + redirectUrl);
            
        } catch (Exception e) {
            // Authentification échouée - affichage d'un message d'erreur sécurisé
            // On ne précise pas si c'est l'email ou le mot de passe qui est incorrect
            // pour des raisons de sécurité
            req.setAttribute("error", "Email ou mot de passe incorrect");
            req.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(req, resp);
        }
    }
    
    /**
     * Détermine l'URL de redirection appropriée selon le rôle de l'utilisateur.
     * 
     * Cette méthode permet de rediriger les utilisateurs vers des pages différentes
     * selon leur niveau d'accès dans l'application.
     * 
     * @param userRole Le rôle de l'utilisateur authentifié
     * @return L'URL relative de redirection
     * 
     * <p><strong>Redirections par rôle :</strong></p>
     * <ul>
     *   <li><code>ADMIN</code> : Page d'administration</li>
     *   <li><code>ORGANIZER</code> : Dashboard organisateur</li>
     *   <li><code>PARTICIPANT</code> : Page d'accueil participant</li>
     *   <li>Autres : Page d'accueil par défaut</li>
     * </ul>
     */
    private String determineRedirectUrl(String userRole) {
        if (userRole == null) {
            return "/"; // Page d'accueil par défaut
        }
        
        switch (userRole.toUpperCase()) {
            case "ADMIN":
                return "/admin"; // Page d'administration
            case "ORGANIZER":  
                return "/organizer-dashboard"; // Dashboard organisateur
            case "PARTICIPANT":
            default:
                return "/"; // Page d'accueil pour les participants et par défaut
        }
    }
    
    /**
     * Traite les requêtes GET de la page de connexion.
     * 
     * Cette méthode redirige vers la page d'accueil qui contient le formulaire
     * de connexion. Elle est appelée quand un utilisateur accède directement
     * à l'URL /login via GET.
     * 
     * @param req La requête HTTP GET
     * @param resp La réponse HTTP pour la redirection
     * @throws ServletException Si erreur lors du traitement de la servlet
     * @throws IOException Si erreur lors des opérations d'E/S
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Configuration de l'encodage
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        
        // Vérification si l'utilisateur est déjà connecté via token
        if (TokenManager.isAuthenticated(req)) {
            UserToken token = TokenManager.getToken(req);
            String redirectUrl = determineRedirectUrl(token.getRole());
            resp.sendRedirect(req.getContextPath() + redirectUrl);
            return;
        }
        
        // Utilisateur non connecté - affichage de la page de connexion
        req.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(req, resp);
    }
}
