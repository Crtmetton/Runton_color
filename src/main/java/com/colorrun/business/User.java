package com.colorrun.business;

/**
 * Représente un utilisateur de l'application Color Run.
 * 
 * Cette classe encapsule toutes les informations d'un utilisateur, incluant
 * ses données personnelles, son rôle dans l'application et son statut d'activation.
 * Elle sert de modèle principal pour la gestion des comptes utilisateurs.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class User {
    /** Identifiant unique de l'utilisateur en base de données */
    private int id;
    
    /** Prénom de l'utilisateur */
    private String firstName;
    
    /** Nom de famille de l'utilisateur */
    private String lastName;
    
    /** Adresse email unique de l'utilisateur (utilisée pour la connexion) */
    private String email;
    
    /** Hash sécurisé du mot de passe (BCrypt) */
    private String passwordHash;
    
    /** Rôle de l'utilisateur dans l'application (ADMIN, ORGANIZER, PARTICIPANT) */
    private String role;
    
    /** URL ou chemin vers la photo de profil de l'utilisateur */
    private String profilePicture;
    
    /** Indique si le compte utilisateur est activé et peut se connecter */
    private boolean enabled;

    /**
     * Constructeur par défaut.
     * Crée un utilisateur avec des valeurs par défaut.
     */
    public User() {
        this.enabled = true; // Par défaut, les utilisateurs sont activés
        this.role = "PARTICIPANT"; // Rôle par défaut
    }

    /**
     * Constructeur complet pour créer un utilisateur avec toutes ses informations.
     * 
     * @param firstName Le prénom de l'utilisateur
     * @param lastName Le nom de famille de l'utilisateur
     * @param email L'adresse email unique de l'utilisateur
     * @param passwordHash Le hash du mot de passe (doit être crypté avant l'appel)
     * @param role Le rôle de l'utilisateur dans l'application
     */
    public User(String firstName, String lastName, String email, String passwordHash, String role) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Retourne l'identifiant unique de l'utilisateur.
     * 
     * @return L'ID de l'utilisateur en base de données
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'utilisateur.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID de l'utilisateur en base de données
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne le prénom de l'utilisateur.
     * 
     * @return Le prénom de l'utilisateur
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Définit le prénom de l'utilisateur.
     * 
     * @param firstName Le prénom de l'utilisateur (ne doit pas être vide)
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retourne le nom de famille de l'utilisateur.
     * 
     * @return Le nom de famille de l'utilisateur
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Définit le nom de famille de l'utilisateur.
     * 
     * @param lastName Le nom de famille de l'utilisateur (ne doit pas être vide)
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retourne l'adresse email de l'utilisateur.
     * 
     * @return L'adresse email unique de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    /**
     * Définit l'adresse email de l'utilisateur.
     * Cette adresse doit être unique dans l'application.
     * 
     * @param email L'adresse email de l'utilisateur (doit être valide et unique)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retourne le hash sécurisé du mot de passe.
     * 
     * @return Le hash BCrypt du mot de passe
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Définit le hash sécurisé du mot de passe.
     * Le mot de passe doit être hashé avec BCrypt avant d'appeler cette méthode.
     * 
     * @param passwordHash Le hash sécurisé du mot de passe
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Retourne le rôle de l'utilisateur dans l'application.
     * 
     * @return Le rôle de l'utilisateur (ADMIN, ORGANIZER, PARTICIPANT)
     */
    public String getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur dans l'application.
     * 
     * @param role Le rôle de l'utilisateur (ADMIN, ORGANIZER, PARTICIPANT)
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Retourne l'URL ou le chemin vers la photo de profil.
     * 
     * @return L'URL/chemin de la photo de profil ou null si aucune photo
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Définit l'URL ou le chemin vers la photo de profil.
     * 
     * @param profilePicture L'URL ou le chemin vers la photo de profil
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Alias pour setProfilePicture() - pour compatibilité avec les servlets.
     * 
     * @param photoUrl L'URL ou le chemin vers la photo de profil
     */
    public void setPhotoUrl(String photoUrl) {
        setProfilePicture(photoUrl);
    }

    /**
     * Indique si le compte utilisateur est activé.
     * 
     * @return true si le compte est activé, false sinon
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Active ou désactive le compte utilisateur.
     * Un compte désactivé ne peut pas se connecter à l'application.
     * 
     * @param enabled true pour activer le compte, false pour le désactiver
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Retourne le nom complet de l'utilisateur (prénom + nom).
     * 
     * @return Le nom complet de l'utilisateur
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Vérifie si l'utilisateur a le rôle d'administrateur.
     * 
     * @return true si l'utilisateur est administrateur, false sinon
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * Vérifie si l'utilisateur a le rôle d'organisateur.
     * 
     * @return true si l'utilisateur est organisateur, false sinon
     */
    public boolean isOrganizer() {
        return "ORGANIZER".equals(role);
    }

    /**
     * Vérifie si l'utilisateur a le rôle de participant.
     * 
     * @return true si l'utilisateur est participant, false sinon
     */
    public boolean isParticipant() {
        return "PARTICIPANT".equals(role);
    }

    /**
     * Retourne une représentation textuelle de l'utilisateur.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales de l'utilisateur
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }
} 