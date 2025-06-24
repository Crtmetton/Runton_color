package com.colorrun.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    // Nombre d'itérations pour le hachage
    private static final int WORKLOAD = 12;
    
    /**
     * Hache un mot de passe en utilisant BCrypt
     * @param password Mot de passe en clair
     * @return Mot de passe haché
     */
    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(password, salt);
    }
    
    /**
     * Vérifie si un mot de passe en clair correspond à un mot de passe haché
     * @param password Mot de passe en clair
     * @param hashedPassword Mot de passe haché
     * @return true si le mot de passe correspond, false sinon
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
} 