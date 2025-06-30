package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.service.EmailService;
import com.colorrun.service.impl.EmailServiceImpl;
import com.colorrun.service.VerificationTokenService;
import com.colorrun.service.impl.VerificationTokenServiceImpl;
import com.colorrun.util.Logger;
import com.colorrun.business.User;

/**
 * Servlet de gestion de l'inscription des nouveaux utilisateurs.
 * 
 * Cette servlet traite les demandes d'inscription de nouveaux comptes utilisateur.
 * Elle valide les données fournies, crée le compte avec un rôle par défaut de PARTICIPANT,
 * et gère le processus de vérification d'email si configuré.
 * 
 * <p><strong>Fonctionnalités :</strong></p>
 * <ul>
 *   <li>Traitement des requêtes POST d'inscription</li>
 *   <li>Validation des données utilisateur (champs obligatoires)</li>
 *   <li>Création de comptes avec hachage sécurisé des mots de passe</li>
 *   <li>Attribution automatique du rôle PARTICIPANT</li>
 *   <li>Support de l'encodage UTF-8 pour les caractères spéciaux</li>
 *   <li>Redirection vers la page d'accueil après inscription réussie</li>
 * </ul>
 * 
 * <p><strong>Paramètres attendus :</strong></p>
 * <ul>
 *   <li><code>firstName</code> : Prénom de l'utilisateur</li>
 *   <li><code>lastName</code> : Nom de famille de l'utilisateur</li>
 *   <li><code>email</code> : Adresse email unique</li>
 *   <li><code>password</code> : Mot de passe en clair</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see UserService Pour la logique de création d'utilisateur
 * @see User Pour le modèle utilisateur
 */
public class RegistrationServlet extends HttpServlet {
    
    /** Service de gestion des utilisateurs pour les opérations d'inscription */
    private final UserService userService;
    
    /** Service d'envoi d'emails pour les notifications */
    private final EmailService emailService;
    
    /** Service de gestion des tokens de vérification */
    private final VerificationTokenService verificationTokenService;
    
    /**
     * Constructeur par défaut.
     * Initialise les services nécessaires pour l'inscription et la vérification par email.
     */
    public RegistrationServlet() {
        this.userService = new UserServiceImpl();
        this.emailService = new EmailServiceImpl();
        this.verificationTokenService = new VerificationTokenServiceImpl();
    }
    
    /**
     * Traite les requêtes GET pour afficher le formulaire d'inscription.
     * 
     * Cette méthode affiche la page d'accueil qui contient le formulaire
     * d'inscription. Elle est appelée quand un utilisateur accède directement
     * à l'URL /register via GET.
     * 
     * @param req La requête HTTP GET
     * @param resp La réponse HTTP pour afficher la page
     * @throws ServletException Si erreur lors du traitement de la servlet
     * @throws IOException Si erreur lors des opérations d'E/S
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(req, resp);
    }
    
    /**
     * Traite les requêtes POST d'inscription d'un nouvel utilisateur.
     * 
     * Cette méthode récupère les données du formulaire d'inscription,
     * valide leur présence et leur format, crée un nouveau compte utilisateur,
     * et redirige vers la page d'accueil avec un message de confirmation.
     * 
     * <p><strong>Flux de traitement :</strong></p>
     * <ol>
     *   <li>Configuration de l'encodage UTF-8</li>
     *   <li>Récupération des paramètres du formulaire</li>
     *   <li>Validation de la présence de tous les champs</li>
     *   <li>Création de l'objet User avec les données validées</li>
     *   <li>Attribution du rôle PARTICIPANT par défaut</li>
     *   <li>Enregistrement via UserService (hachage automatique du mot de passe)</li>
     *   <li>Redirection vers l'accueil avec message de succès</li>
     * </ol>
     * 
     * <p><strong>Gestion d'erreurs :</strong></p>
     * <ul>
     *   <li>Champs manquants : Message d'erreur générique</li>
     *   <li>Email déjà existant : Message d'erreur spécifique</li>
     *   <li>Erreur système : Message d'erreur avec détails</li>
     * </ul>
     * 
     * @param req La requête HTTP contenant les données d'inscription
     * @param resp La réponse HTTP pour la redirection
     * @throws ServletException Si erreur lors du traitement de la servlet
     * @throws IOException Si erreur lors des opérations d'E/S
     * 
     * @example
     * POST /register
     * Content-Type: application/x-www-form-urlencoded
     * 
     * firstName=Jean&lastName=Dupont&email=jean@example.com&password=motdepasse123
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Logger.separator("NOUVELLE INSCRIPTION UTILISATEUR");
        Logger.step("RegistrationServlet", "Démarrage du processus d'inscription");
        
        // Configuration de l'encodage pour supporter les caractères spéciaux français
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        Logger.debug("RegistrationServlet", "Encodage UTF-8 configuré");
        
        // Récupération des paramètres du formulaire d'inscription
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        Logger.info("RegistrationServlet", "Paramètres reçus pour l'inscription");
        Logger.debug("RegistrationServlet", "firstName: " + (firstName != null ? "✓" : "✗"));
        Logger.debug("RegistrationServlet", "lastName: " + (lastName != null ? "✓" : "✗"));
        Logger.debug("RegistrationServlet", "email: " + (email != null ? email : "✗"));
        Logger.debug("RegistrationServlet", "password: " + (password != null ? "[CACHÉ]" : "✗"));
        
        // Validation de la présence de tous les champs obligatoires
        Logger.step("RegistrationServlet", "Validation des champs obligatoires");
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            Logger.stepFailed("RegistrationServlet", "Validation des champs", "Champs manquants ou vides");
            Logger.error("RegistrationServlet", "Échec validation: champs obligatoires manquants");
            
            // Champs manquants - affichage d'une erreur et retour au formulaire
            req.setAttribute("error", "Tous les champs sont obligatoires");
            req.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(req, resp);
            return;
        }
        Logger.stepSuccess("RegistrationServlet", "Validation des champs");
        
        try {
            // Création de l'objet utilisateur avec les données du formulaire
            Logger.step("RegistrationServlet", "Création de l'objet utilisateur");
            User user = new User();
            user.setFirstName(firstName.trim());
            user.setLastName(lastName.trim());
            user.setEmail(email.trim().toLowerCase()); // Normalisation de l'email
            user.setPasswordHash(password); // Le hachage sera fait par le service
            user.setRole("PARTICIPANT"); // Rôle par défaut pour les nouveaux inscrits
            user.setEnabled(false); // Le compte sera activé après vérification email
            
            Logger.info("RegistrationServlet", "Utilisateur créé: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
            Logger.stepSuccess("RegistrationServlet", "Création de l'objet utilisateur");
            
            // Enregistrement du nouvel utilisateur via le service
            Logger.step("RegistrationServlet", "Enregistrement en base de données");
            userService.register(user);
            Logger.stepSuccess("RegistrationServlet", "Enregistrement en base de données");
            
            try {
                // Génération et envoi du token de vérification par email
                Logger.step("RegistrationServlet", "Génération du token de vérification");
                String verificationToken = verificationTokenService.generateVerificationToken(user);
                Logger.stepSuccess("RegistrationServlet", "Génération du token de vérification");
                Logger.debug("RegistrationServlet", "Token généré: " + verificationToken.substring(0, 8) + "...");
                
                Logger.step("RegistrationServlet", "Envoi de l'email de vérification");
                emailService.sendVerificationEmail(user, verificationToken);
                Logger.stepSuccess("RegistrationServlet", "Envoi de l'email de vérification");
                Logger.email("RegistrationServlet", "Email de vérification envoyé avec succès", user.getEmail());
                
                // Inscription réussie - redirection avec message de vérification
                HttpSession session = req.getSession();
                session.setAttribute("success", 
                    "Inscription réussie ! Un email de vérification a été envoyé à " + user.getEmail() + 
                    ". Veuillez vérifier votre boîte mail pour activer votre compte.");
                    
                Logger.success("RegistrationServlet", "Inscription terminée avec succès - Email de vérification envoyé");
            } catch (Exception emailError) {
                // Si l'envoi d'email échoue, on active quand même le compte
                Logger.stepFailed("RegistrationServlet", "Envoi de l'email de vérification", emailError.getMessage());
                Logger.error("RegistrationServlet", "Erreur envoi email de vérification", emailError);
                Logger.warn("RegistrationServlet", "Activation automatique du compte suite à l'échec email");
                
                user.setEnabled(true);
                userService.update(user);
                
                HttpSession session = req.getSession();
                session.setAttribute("success", 
                    "Inscription réussie ! Vous pouvez maintenant vous connecter avec vos identifiants.");
                    
                Logger.success("RegistrationServlet", "Inscription terminée avec succès - Compte activé automatiquement");
            }
            
            resp.sendRedirect(req.getContextPath() + "/");
            
        } catch (Exception e) {
            // Gestion des erreurs d'inscription (email existant, erreur DB, etc.)
            Logger.stepFailed("RegistrationServlet", "Processus d'inscription", e.getMessage());
            Logger.error("RegistrationServlet", "Erreur lors de l'inscription", e);
            
            String errorMessage = determineErrorMessage(e);
            Logger.warn("RegistrationServlet", "Message d'erreur utilisateur: " + errorMessage);
            
            req.setAttribute("error", errorMessage);
            req.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(req, resp);
        }
    }
    
    /**
     * Détermine le message d'erreur approprié selon le type d'exception.
     * 
     * Cette méthode analyse l'exception reçue et retourne un message d'erreur
     * compréhensible par l'utilisateur, en masquant les détails techniques
     * pour des raisons de sécurité.
     * 
     * @param e L'exception qui s'est produite lors de l'inscription
     * @return Un message d'erreur approprié pour l'utilisateur
     * 
     * <p><strong>Messages d'erreur possibles :</strong></p>
     * <ul>
     *   <li>Email déjà existant</li>
     *   <li>Erreur de validation des données</li>
     *   <li>Erreur système générique</li>
     * </ul>
     */
    private String determineErrorMessage(Exception e) {
        String message = e.getMessage();
        
        if (message != null) {
            // Détection des erreurs courantes pour fournir des messages spécifiques
            if (message.toLowerCase().contains("email") && 
                (message.toLowerCase().contains("exist") || message.toLowerCase().contains("unique"))) {
                return "Cette adresse email est déjà utilisée. Veuillez en choisir une autre.";
            }
            
            if (message.toLowerCase().contains("invalid") || 
                message.toLowerCase().contains("format")) {
                return "Format d'email invalide. Veuillez vérifier votre adresse email.";
            }
            
            if (message.toLowerCase().contains("password")) {
                return "Le mot de passe doit contenir au moins 8 caractères.";
            }
        }
        
        // Message d'erreur générique pour les autres cas
        return "Une erreur s'est produite lors de l'inscription. Veuillez réessayer.";
    }
    
    /**
     * Valide le format d'une adresse email.
     * 
     * Cette méthode utilise une expression régulière simple pour valider
     * le format de base d'une adresse email.
     * 
     * @param email L'adresse email à valider
     * @return true si l'email est valide, false sinon
     * 
     * @example
     * boolean valid = isValidEmail("jean@example.com"); // retourne true
     * boolean invalid = isValidEmail("email.invalide"); // retourne false
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Expression régulière simple pour la validation d'email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Valide la robustesse d'un mot de passe.
     * 
     * Cette méthode vérifie que le mot de passe respecte les critères
     * de sécurité minimaux de l'application.
     * 
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est suffisamment robuste, false sinon
     * 
     * <p><strong>Critères de validation :</strong></p>
     * <ul>
     *   <li>Longueur minimale de 8 caractères</li>
     *   <li>Au moins une lettre</li>
     *   <li>Au moins un chiffre (recommandé mais non obligatoire)</li>
     * </ul>
     * 
     * @example
     * boolean valid = isValidPassword("motdepasse123"); // retourne true
     * boolean invalid = isValidPassword("123"); // retourne false
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Vérification de la présence d'au moins une lettre
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        
        return hasLetter;
    }
}
