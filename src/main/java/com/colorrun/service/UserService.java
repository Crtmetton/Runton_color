package com.colorrun.service;

import com.colorrun.business.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs de l'application Color Run.
 * 
 * Cette interface définit toutes les opérations relatives à la gestion des comptes utilisateurs,
 * incluant l'inscription, l'authentification, la gestion des profils et des rôles.
 * Elle sert de contrat pour l'implémentation de la logique métier liée aux utilisateurs.
 * 
 * Le service gère les différents types d'utilisateurs :
 * - ADMIN : Administrateurs avec tous les droits
 * - ORGANIZER : Organisateurs pouvant créer des courses
 * - PARTICIPANT : Participants aux courses Color Run
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public interface UserService {
    
    /**
     * Inscrit un nouvel utilisateur dans l'application.
     * 
     * Cette méthode crée un nouveau compte utilisateur avec un rôle par défaut
     * de PARTICIPANT. Le mot de passe est automatiquement haché avant le stockage.
     * L'email doit être unique dans le système.
     * 
     * @param user L'utilisateur à inscrire (avec mot de passe en clair)
     * @return L'utilisateur créé avec son ID assigné
     * @throws SQLException si erreur lors de l'accès à la base de données
     * @throws IllegalArgumentException si l'email existe déjà ou si les données sont invalides
     * 
     * @example
     * User newUser = new User("Jean", "Dupont", "jean@example.com", "motdepasse123", "PARTICIPANT");
     * User registeredUser = userService.register(newUser);
     */
    User register(User user) throws SQLException;
    
    /**
     * Authentifie un utilisateur avec son email et mot de passe.
     * 
     * Cette méthode vérifie les identifiants fournis et retourne l'utilisateur
     * si l'authentification réussit. Le compte doit être activé pour que
     * l'authentification soit possible.
     * 
     * @param email L'adresse email de l'utilisateur
     * @param password Le mot de passe en clair
     * @return L'utilisateur authentifié si les identifiants sont corrects
     * @throws SQLException si erreur lors de l'accès à la base de données
     * @throws IllegalArgumentException si les identifiants sont incorrects ou le compte désactivé
     * 
     * @example
     * User authenticatedUser = userService.authenticate("jean@example.com", "motdepasse123");
     */
    User authenticate(String email, String password) throws SQLException;
    
    /**
     * Recherche un utilisateur par son identifiant unique.
     * 
     * @param id L'identifiant unique de l'utilisateur
     * @return Un Optional contenant l'utilisateur trouvé, ou Optional.empty() si non trouvé
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * Optional<User> user = userService.findById(123);
     * if (user.isPresent()) {
     *     System.out.println("Utilisateur trouvé : " + user.get().getFullName());
     * }
     */
    Optional<User> findById(int id) throws SQLException;
    
    /**
     * Recherche un utilisateur par son adresse email.
     * 
     * L'email étant unique dans le système, cette méthode retourne
     * au maximum un utilisateur.
     * 
     * @param email L'adresse email à rechercher
     * @return Un Optional contenant l'utilisateur trouvé, ou Optional.empty() si non trouvé
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * Optional<User> user = userService.findByEmail("jean@example.com");
     */
    Optional<User> findByEmail(String email) throws SQLException;
    
    /**
     * Récupère tous les utilisateurs ayant un rôle spécifique.
     * 
     * @param role Le rôle des utilisateurs à rechercher (ADMIN, ORGANIZER, PARTICIPANT)
     * @return La liste des utilisateurs ayant ce rôle
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * List<User> organizers = userService.findByRole("ORGANIZER");
     */
    List<User> findByRole(String role) throws SQLException;
    
    /**
     * Récupère tous les utilisateurs du système.
     * 
     * Cette méthode est généralement utilisée par les administrateurs
     * pour la gestion globale des comptes.
     * 
     * @return La liste de tous les utilisateurs
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * List<User> allUsers = userService.findAll();
     */
    List<User> findAll() throws SQLException;
    
    /**
     * Met à jour les informations d'un utilisateur existant.
     * 
     * Cette méthode permet de modifier le profil d'un utilisateur.
     * Le mot de passe n'est pas modifié par cette méthode.
     * 
     * @param user L'utilisateur avec les nouvelles informations
     * @return L'utilisateur mis à jour
     * @throws SQLException si erreur lors de l'accès à la base de données
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     * 
     * @example
     * user.setFirstName("Nouveau Prénom");
     * User updatedUser = userService.update(user);
     */
    User update(User user) throws SQLException;

    /**
     * Met à jour le profil d'un utilisateur (alias pour update).
     * 
     * @param user L'utilisateur avec les nouvelles informations
     * @return L'utilisateur mis à jour
     * @throws SQLException si erreur lors de l'accès à la base de données
     */
    User updateProfile(User user) throws SQLException;
    
    /**
     * Change le mot de passe d'un utilisateur.
     * 
     * Cette méthode vérifie d'abord l'ancien mot de passe avant d'appliquer
     * le nouveau. Le nouveau mot de passe est automatiquement haché.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param oldPassword L'ancien mot de passe (en clair)
     * @param newPassword Le nouveau mot de passe (en clair)
     * @return true si le changement a réussi, false si l'ancien mot de passe est incorrect
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * boolean success = userService.changePassword(123, "ancienMdp", "nouveauMdp123");
     */
    boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException;
    
    /**
     * Réinitialise le mot de passe d'un utilisateur.
     * 
     * Cette méthode génère un nouveau mot de passe temporaire et l'envoie
     * par email à l'utilisateur. Utilisée quand l'utilisateur a oublié son mot de passe.
     * 
     * @param email L'adresse email de l'utilisateur
     * @return true si la réinitialisation a réussi, false si l'email n'existe pas
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * boolean sent = userService.resetPassword("jean@example.com");
     */
    boolean resetPassword(String email) throws SQLException;
    
    /**
     * Active ou désactive un compte utilisateur.
     * 
     * Un compte désactivé ne peut pas se connecter à l'application.
     * Cette action est généralement réservée aux administrateurs.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param enabled true pour activer, false pour désactiver
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * userService.setEnabled(123, false); // Désactive le compte
     */
    void setEnabled(int userId, boolean enabled) throws SQLException;
    
    /**
     * Change le rôle d'un utilisateur.
     * 
     * Cette action est généralement réservée aux administrateurs.
     * Les rôles possibles sont : ADMIN, ORGANIZER, PARTICIPANT.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param newRole Le nouveau rôle à assigner
     * @throws SQLException si erreur lors de l'accès à la base de données
     * @throws IllegalArgumentException si le rôle n'est pas valide
     * 
     * @example
     * userService.changeRole(123, "ORGANIZER"); // Promeut en organisateur
     */
    void changeRole(int userId, String newRole) throws SQLException;
    
    /**
     * Supprime définitivement un compte utilisateur.
     * 
     * Cette action est irréversible et supprime toutes les données
     * associées à l'utilisateur (participations, messages, etc.).
     * Généralement réservée aux administrateurs.
     * 
     * @param userId L'identifiant de l'utilisateur à supprimer
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * userService.delete(123); // Supprime définitivement l'utilisateur
     */
    void delete(int userId) throws SQLException;
    
    /**
     * Vérifie si un email est déjà utilisé par un autre utilisateur.
     * 
     * Utile lors de l'inscription ou de la modification d'email
     * pour s'assurer de l'unicité.
     * 
     * @param email L'adresse email à vérifier
     * @return true si l'email existe déjà, false sinon
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * if (!userService.emailExists("nouveau@example.com")) {
     *     // L'email est disponible
     * }
     */
    boolean emailExists(String email) throws SQLException;
    
    /**
     * Envoie un email de vérification à un utilisateur.
     * 
     * Cette méthode est appelée lors de l'inscription pour confirmer
     * l'adresse email de l'utilisateur.
     * 
     * @param user L'utilisateur à qui envoyer l'email
     * @param baseUrl L'URL de base de l'application pour le lien de vérification
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * userService.sendVerificationEmail(newUser, "https://colorrun.com");
     */
    void sendVerificationEmail(User user, String baseUrl) throws SQLException;
    
    /**
     * Vérifie un token de vérification d'email.
     * 
     * Cette méthode active le compte utilisateur si le token est valide.
     * 
     * @param token Le token de vérification reçu par email
     * @return true si la vérification a réussi, false si le token est invalide ou expiré
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * boolean verified = userService.verifyEmail("abc123def456");
     */
    boolean verifyEmail(String token) throws SQLException;
    
    /**
     * Compte le nombre total d'utilisateurs dans le système.
     * 
     * Utile pour les statistiques et la gestion administrative.
     * 
     * @return Le nombre total d'utilisateurs
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * int totalUsers = userService.countUsers();
     * System.out.println("Nombre d'utilisateurs : " + totalUsers);
     */
    int countUsers() throws SQLException;
    
    /**
     * Compte le nombre d'utilisateurs ayant un rôle spécifique.
     * 
     * @param role Le rôle à compter (ADMIN, ORGANIZER, PARTICIPANT)
     * @return Le nombre d'utilisateurs ayant ce rôle
     * @throws SQLException si erreur lors de l'accès à la base de données
     * 
     * @example
     * int organizerCount = userService.countUsersByRole("ORGANIZER");
     */
    int countUsersByRole(String role) throws SQLException;
} 