package com.colorrun.security;

import com.colorrun.business.User;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Token de session utilisateur contenant toutes les informations nécessaires
 * pour l'authentification et l'autorisation
 */
public class UserToken {
    
    private String tokenId;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role; // USER, ORGANISATEUR, ADMIN
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private boolean isValid;
    
    /**
     * Constructeur pour créer un token à partir d'un utilisateur
     */
    public UserToken(User user) {
        this.tokenId = UUID.randomUUID().toString();
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.isValid = true;
    }
    
    /**
     * Vérifie si l'utilisateur est connecté et le token valide
     */
    public boolean isAuthenticated() {
        return isValid && !isExpired();
    }
    
    /**
     * Vérifie si l'utilisateur est administrateur
     */
    public boolean isAdmin() {
        return isAuthenticated() && "ADMIN".equals(role);
    }
    
    /**
     * Vérifie si l'utilisateur est organisateur (ou admin)
     */
    public boolean isOrganizer() {
        return isAuthenticated() && ("ORGANISATEUR".equals(role) || "ADMIN".equals(role));
    }
    
    /**
     * Vérifie si l'utilisateur est un simple utilisateur
     */
    public boolean isUser() {
        return isAuthenticated() && "USER".equals(role);
    }
    
    /**
     * Vérifie si le token a expiré (24h)
     */
    public boolean isExpired() {
        return lastActivity.isBefore(LocalDateTime.now().minusHours(24));
    }
    
    /**
     * Met à jour l'activité du token
     */
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    /**
     * Invalide le token (déconnexion)
     */
    public void invalidate() {
        this.isValid = false;
    }
    
    /**
     * Retourne le nom complet de l'utilisateur
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Retourne une description du rôle en français
     */
    public String getRoleDescription() {
        switch (role) {
            case "ADMIN": return "Administrateur";
            case "ORGANIZER": return "Organisateur";
            case "PARTICIPANT": return "Utilisateur";
            default: return "Inconnu";
        }
    }
    
    // Getters et Setters
    public String getTokenId() { return tokenId; }
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastActivity() { return lastActivity; }
    public boolean isValid() { return isValid; }
    
    @Override
    public String toString() {
        return String.format("UserToken{id='%s', user='%s %s', role='%s', valid=%s}", 
                           tokenId.substring(0, 8), firstName, lastName, role, isValid);
    }
} 