package com.colorrun.service.impl;

import com.colorrun.business.User;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.UserService;
import com.colorrun.util.PasswordUtil;
import com.colorrun.config.EmailConfig;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import javax.mail.MessagingException;

/**
 * Implémentation du service de gestion des utilisateurs.
 * 
 * Cette classe implémente la logique métier pour toutes les opérations relatives
 * aux utilisateurs de l'application Color Run. Elle fait le lien entre la couche
 * de présentation (servlets) et la couche d'accès aux données (DAO).
 * 
 * <p><strong>Responsabilités principales :</strong></p>
 * <ul>
 *   <li>Gestion de l'inscription et de l'authentification</li>
 *   <li>Validation des données utilisateur</li>
 *   <li>Orchestration des appels au DAO</li>
 *   <li>Gestion des mots de passe (hachage, vérification)</li>
 *   <li>Envoi d'emails de vérification (si configuré)</li>
 *   <li>Gestion des rôles et permissions</li>
 * </ul>
 * 
 * <p><strong>Règles métier appliquées :</strong></p>
 * <ul>
 *   <li>Unicité des adresses email</li>
 *   <li>Hachage sécurisé des mots de passe avec BCrypt</li>
 *   <li>Attribution automatique du rôle PARTICIPANT aux nouveaux inscrits</li>
 *   <li>Validation de la robustesse des mots de passe</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see UserService Pour l'interface du service
 * @see UserDAO Pour l'accès aux données
 * @see PasswordUtil Pour le hachage des mots de passe
 */
public class UserServiceImpl implements UserService {
    
    /** DAO pour l'accès aux données utilisateur */
    private final UserDAO userDAO;
    
    /**
     * Constructeur par défaut.
     * Initialise le DAO utilisateur pour les opérations de persistance.
     */
    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Cette implémentation vérifie l'unicité de l'email, valide les données
     * d'entrée, hache le mot de passe de manière sécurisée et assigne
     * automatiquement le rôle PARTICIPANT.
     * 
     * @param user L'utilisateur à inscrire avec ses informations de base
     * @return L'utilisateur créé avec son ID assigné
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * @throws IllegalArgumentException Si les données sont invalides ou l'email existe déjà
     */
    @Override
    public User register(User user) throws SQLException {
        // Validation des données d'entrée
        validateUserData(user);
        
        // Vérification de l'unicité de l'email
        if (userDAO.emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée.");
        }
        
        // Configuration des valeurs par défaut
        user.setRole("PARTICIPANT"); // Rôle par défaut pour les nouveaux inscrits
        user.setEnabled(true); // Compte activé par défaut
        
        // Validation du mot de passe et hachage
        validatePassword(user.getPasswordHash());
        
        // Enregistrement en base de données via le DAO
        return userDAO.save(user);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Cette implémentation vérifie les identifiants fournis, contrôle que
     * le compte est activé, et retourne l'utilisateur authentifié.
     * 
     * @param email L'adresse email de l'utilisateur
     * @param password Le mot de passe en clair
     * @return L'utilisateur authentifié
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * @throws IllegalArgumentException Si les identifiants sont incorrects ou le compte désactivé
     */
    @Override
    public User authenticate(String email, String password) throws SQLException {
        // Validation des paramètres d'entrée
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email est obligatoire");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        
        // Recherche de l'utilisateur par email
        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Identifiants incorrects");
        }
        
        User user = userOpt.get();
        
        // Vérification que le compte est activé
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("Compte désactivé. Contactez l'administrateur.");
        }
        
        // Vérification du mot de passe
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Identifiants incorrects");
        }
        
        return user;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findById(int id) throws SQLException {
        return userDAO.findById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userDAO.findByEmail(email.trim().toLowerCase());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findByRole(String role) throws SQLException {
        return userDAO.findByRole(role);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() throws SQLException {
        return userDAO.findAll();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Cette implémentation valide les nouvelles données et vérifie l'unicité
     * de l'email s'il a été modifié.
     */
    @Override
    public User update(User user) throws SQLException {
        // Validation des données
        validateUserDataForUpdate(user);
        
        // Vérification de l'unicité de l'email si modifié
        Optional<User> existingUser = userDAO.findById(user.getId());
        if (existingUser.isPresent()) {
            String oldEmail = existingUser.get().getEmail();
            String newEmail = user.getEmail();
            
            if (!oldEmail.equals(newEmail) && userDAO.emailExists(newEmail)) {
                throw new IllegalArgumentException("Cette adresse email est déjà utilisée.");
            }
        }
        
        // Mise à jour via le DAO
        userDAO.update(user);
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updateProfile(User user) throws SQLException {
        return update(user);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Cette implémentation vérifie d'abord l'ancien mot de passe avant
     * d'appliquer le nouveau avec un hachage sécurisé.
     */
    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        // Récupération de l'utilisateur
        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Vérification de l'ancien mot de passe
        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            return false; // Ancien mot de passe incorrect
        }
        
        // Validation du nouveau mot de passe
        validatePassword(newPassword);
        
        // Mise à jour du mot de passe
        userDAO.updatePassword(userId, newPassword);
        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Cette implémentation génère un mot de passe temporaire aléatoire
     * et l'envoie par email à l'utilisateur.
     */
    @Override
    public boolean resetPassword(String email) throws SQLException {
        // Recherche de l'utilisateur
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (!userOpt.isPresent()) {
            return false; // Email non trouvé
        }
        
        User user = userOpt.get();
        
        // Génération d'un mot de passe temporaire
        String tempPassword = PasswordUtil.generateTemporaryPassword(12);
        
        // Mise à jour du mot de passe en base
        userDAO.updatePassword(user.getId(), tempPassword);
        
        // Envoi du nouveau mot de passe par email
        // Note: L'envoi d'email est temporairement désactivé
        // sendPasswordResetEmail(user, tempPassword);
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(int userId, boolean enabled) throws SQLException {
        userDAO.setEnabled(userId, enabled);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Cette implémentation valide que le nouveau rôle est autorisé.
     */
    @Override
    public void changeRole(int userId, String newRole) throws SQLException {
        // Validation du rôle
        if (!isValidRole(newRole)) {
            throw new IllegalArgumentException("Rôle invalide: " + newRole);
        }
        
        userDAO.updateRole(userId, newRole);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(int userId) throws SQLException {
        userDAO.delete(userId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean emailExists(String email) throws SQLException {
        return userDAO.emailExists(email);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Note: L'envoi d'email est temporairement désactivé pour simplifier
     * le processus d'inscription.
     */
    @Override
    public void sendVerificationEmail(User user, String baseUrl) throws SQLException {
        // TODO: Implémenter l'envoi d'email de vérification
        // Pour l'instant, cette fonctionnalité est désactivée
        
        /* Code pour l'envoi d'email (à réactiver si nécessaire):
        String token = UUID.randomUUID().toString();
        userDAO.saveVerificationToken(user.getId(), token);
        
        String verificationUrl = baseUrl + "/verify-email?token=" + token;
        sendEmail(user.getEmail(), "Vérification de votre compte Color Run", 
                 "Cliquez sur ce lien pour vérifier votre compte: " + verificationUrl);
        */
    }
    
    /**
     * {@inheritDoc}
     * 
     * Note: La vérification par email est temporairement désactivée.
     */
    @Override
    public boolean verifyEmail(String token) throws SQLException {
        // TODO: Implémenter la vérification par token
        // Pour l'instant, cette fonctionnalité est désactivée
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countUsers() throws SQLException {
        return userDAO.count();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countUsersByRole(String role) throws SQLException {
        return userDAO.countByRole(role);
    }
    
    /**
     * Valide les données d'un utilisateur lors de l'inscription.
     * 
     * Cette méthode vérifie que tous les champs obligatoires sont présents
     * et que les données respectent les contraintes métier.
     * 
     * @param user L'utilisateur à valider
     * @throws IllegalArgumentException Si les données sont invalides
     */
    private void validateUserData(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Les données utilisateur ne peuvent pas être nulles");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de famille est obligatoire");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email est obligatoire");
        }
        
        // Validation du format email
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
    }
    
    /**
     * Valide les données d'un utilisateur lors de la mise à jour.
     * 
     * Similaire à validateUserData mais sans validation du mot de passe
     * car celui-ci n'est pas modifié lors d'une mise à jour de profil.
     * 
     * @param user L'utilisateur à valider
     * @throws IllegalArgumentException Si les données sont invalides
     */
    private void validateUserDataForUpdate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Les données utilisateur ne peuvent pas être nulles");
        }
        
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("ID utilisateur invalide");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de famille est obligatoire");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse email est obligatoire");
        }
        
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
    }
    
    /**
     * Valide la robustesse d'un mot de passe.
     * 
     * Cette méthode applique les règles de sécurité pour les mots de passe
     * de l'application Color Run.
     * 
     * @param password Le mot de passe à valider
     * @throws IllegalArgumentException Si le mot de passe ne respecte pas les critères
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères");
        }
        
        // Vérification de la présence d'au moins une lettre
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre");
        }
    }
    
    /**
     * Valide le format d'une adresse email.
     * 
     * Cette méthode utilise une expression régulière pour vérifier
     * que l'email respecte un format valide.
     * 
     * @param email L'adresse email à valider
     * @return true si l'email est valide, false sinon
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Expression régulière pour la validation d'email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Vérifie si un rôle est valide dans l'application.
     * 
     * @param role Le rôle à vérifier
     * @return true si le rôle est valide, false sinon
     */
    private boolean isValidRole(String role) {
        if (role == null) {
            return false;
        }
        
        return "ADMIN".equals(role) || "ORGANIZER".equals(role) || "PARTICIPANT".equals(role);
    }
    
    /**
     * Envoie un email à un destinataire.
     * 
     * Cette méthode est désactivée temporairement car l'envoi d'email
     * nécessite une configuration SMTP appropriée.
     * 
     * @param to Adresse email du destinataire
     * @param subject Sujet de l'email
     * @param body Corps du message
     * @throws RuntimeException Si l'envoi échoue
     */
    @SuppressWarnings("unused")
    private void sendEmail(String to, String subject, String body) {
        // TODO: Implémenter l'envoi d'email avec la configuration SMTP
        // Cette fonctionnalité est temporairement désactivée
        
        /* Configuration pour l'envoi d'email (à réactiver si nécessaire):
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("email@colorrun.com", "password");
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@colorrun.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }
        */
    }
}