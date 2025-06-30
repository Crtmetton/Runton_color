package com.colorrun.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour la gestion sécurisée des mots de passe.
 * 
 * Cette classe fournit des méthodes statiques pour hacher et vérifier
 * les mots de passe en utilisant l'algorithme BCrypt, qui est considéré
 * comme une méthode sécurisée pour le stockage des mots de passe.
 * 
 * BCrypt est un algorithme de hachage adaptatif qui inclut automatiquement
 * un salt et permet d'ajuster la complexité de calcul pour résister aux
 * attaques par force brute même avec l'augmentation de la puissance de calcul.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class PasswordUtil {
    
    /** 
     * Nombre de rounds pour l'algorithme BCrypt.
     * Une valeur plus élevée augmente la sécurité mais ralentit les calculs.
     * La valeur 12 est un bon compromis entre sécurité et performance en 2024.
     */
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Constructeur privé pour empêcher l'instanciation.
     * Cette classe contient uniquement des méthodes statiques.
     */
    private PasswordUtil() {
        // Classe utilitaire - ne doit pas être instanciée
    }

    /**
     * Hache un mot de passe en clair en utilisant BCrypt.
     * 
     * Cette méthode génère automatiquement un salt unique et hache
     * le mot de passe avec le nombre de rounds configuré. Le résultat
     * contient le salt et peut être directement stocké en base de données.
     * 
     * @param plainTextPassword Le mot de passe en clair à hacher
     * @return Le hash BCrypt du mot de passe, incluant le salt
     * @throws IllegalArgumentException si le mot de passe est null ou vide
     * 
     * @example
     * String hashedPassword = PasswordUtil.hashPassword("monMotDePasse123");
     * // Résultat : "$2a$12$abc123..."
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être null ou vide");
        }
        
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Vérifie qu'un mot de passe en clair correspond à un hash BCrypt.
     * 
     * Cette méthode compare le mot de passe fourni avec le hash stocké
     * en utilisant l'algorithme BCrypt. Elle extrait automatiquement
     * le salt du hash pour effectuer la comparaison.
     * 
     * @param plainTextPassword Le mot de passe en clair à vérifier
     * @param hashedPassword Le hash BCrypt stocké en base de données
     * @return true si le mot de passe correspond au hash, false sinon
     * 
     * @example
     * String userInput = "monMotDePasse123";
     * String storedHash = "$2a$12$abc123...";
     * boolean isValid = PasswordUtil.checkPassword(userInput, storedHash);
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash malformé ou autre erreur
            return false;
        }
    }

    /**
     * Vérifie si un hash de mot de passe est valide et bien formé.
     * 
     * Cette méthode permet de vérifier qu'un string représente bien
     * un hash BCrypt valide avant de l'utiliser ou de le stocker.
     * 
     * @param hashedPassword Le hash à vérifier
     * @return true si le hash est valide, false sinon
     * 
     * @example
     * boolean isValidHash = PasswordUtil.isValidHash("$2a$12$abc123...");
     */
    public static boolean isValidHash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Tente de vérifier avec un mot de passe factice
            // Si le hash est valide, ça ne lèvera pas d'exception
            BCrypt.checkpw("test", hashedPassword);
            return true;
        } catch (IllegalArgumentException e) {
            // Hash malformé
            return false;
        }
    }

    /**
     * Génère un mot de passe temporaire aléatoire.
     * 
     * Utile pour la réinitialisation de mots de passe ou la création
     * de comptes temporaires. Le mot de passe généré contient des
     * lettres majuscules, minuscules et des chiffres.
     * 
     * @param length La longueur souhaitée du mot de passe (minimum 8)
     * @return Un mot de passe temporaire aléatoire
     * @throws IllegalArgumentException si la longueur est inférieure à 8
     * 
     * @example
     * String tempPassword = PasswordUtil.generateTemporaryPassword(12);
     * // Résultat possible : "Kj8mN2pQ9xYz"
     */
    public static String generateTemporaryPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("La longueur minimale du mot de passe est de 8 caractères");
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }

    /**
     * Évalue la force d'un mot de passe selon plusieurs critères.
     * 
     * Cette méthode vérifie la longueur, la présence de différents types
     * de caractères et d'autres critères de sécurité pour évaluer la
     * robustesse d'un mot de passe.
     * 
     * @param password Le mot de passe à évaluer
     * @return Un score de 0 (très faible) à 5 (très fort)
     * 
     * Critères d'évaluation :
     * - Longueur >= 8 caractères : +1 point
     * - Présence de minuscules : +1 point
     * - Présence de majuscules : +1 point
     * - Présence de chiffres : +1 point
     * - Présence de caractères spéciaux : +1 point
     * 
     * @example
     * int strength = PasswordUtil.evaluatePasswordStrength("MonMotDePasse123!");
     * // Retourne 5 (très fort)
     */
    public static int evaluatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // Longueur >= 8
        if (password.length() >= 8) {
            score++;
        }
        
        // Contient des minuscules
        if (password.matches(".*[a-z].*")) {
            score++;
        }
        
        // Contient des majuscules
        if (password.matches(".*[A-Z].*")) {
            score++;
        }
        
        // Contient des chiffres
        if (password.matches(".*[0-9].*")) {
            score++;
        }
        
        // Contient des caractères spéciaux
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            score++;
        }
        
        return score;
    }

    /**
     * Retourne une description textuelle de la force d'un mot de passe.
     * 
     * @param password Le mot de passe à évaluer
     * @return Une description de la force du mot de passe
     * 
     * @example
     * String description = PasswordUtil.getPasswordStrengthDescription("password123");
     * // Retourne "Moyen"
     */
    public static String getPasswordStrengthDescription(String password) {
        int strength = evaluatePasswordStrength(password);
        
        switch (strength) {
            case 0:
            case 1:
                return "Très faible";
            case 2:
                return "Faible";
            case 3:
                return "Moyen";
            case 4:
                return "Fort";
            case 5:
                return "Très fort";
            default:
                return "Indéterminé";
        }
    }
} 