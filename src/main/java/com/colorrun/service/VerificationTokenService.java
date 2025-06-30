package com.colorrun.service;

import com.colorrun.business.User;

/**
 * Service pour la gestion des tokens de vérification d'email.
 * 
 * Ce service gère la création, validation et expiration des tokens
 * utilisés pour vérifier les adresses email lors de l'inscription.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public interface VerificationTokenService {
    
    /**
     * Génère un token de vérification pour un utilisateur.
     * 
     * @param user L'utilisateur pour lequel générer le token
     * @return Le token de vérification généré
     * @throws Exception si erreur lors de la génération
     */
    String generateVerificationToken(User user) throws Exception;
    
    /**
     * Valide un token de vérification et active le compte utilisateur.
     * 
     * @param token Le token à valider
     * @return true si le token est valide et le compte a été activé, false sinon
     * @throws Exception si erreur lors de la validation
     */
    boolean validateToken(String token) throws Exception;
    
    /**
     * Supprime les tokens expirés.
     * 
     * @throws Exception si erreur lors du nettoyage
     */
    void cleanupExpiredTokens() throws Exception;
    
    /**
     * Vérifie si un token existe et est valide (non expiré).
     * 
     * @param token Le token à vérifier
     * @return true si le token existe et est valide, false sinon
     * @throws Exception si erreur lors de la vérification
     */
    boolean isTokenValid(String token) throws Exception;
} 