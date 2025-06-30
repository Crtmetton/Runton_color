package com.colorrun.config;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * Configuration centralisée pour le système d'envoi d'emails de l'application Color Run.
 * 
 * Cette classe encapsule tous les paramètres nécessaires à la configuration
 * du service de messagerie électronique. Elle gère les connexions SMTP,
 * les authentifications et les paramètres de sécurité pour l'envoi d'emails
 * automatiques et de notifications.
 * 
 * <p><strong>Fonctionnalités supportées :</strong></p>
 * <ul>
 *   <li>Configuration SMTP avec authentification</li>
 *   <li>Support TLS/SSL pour la sécurité</li>
 *   <li>Templates d'emails personnalisables</li>
 *   <li>Gestion des adresses d'expédition</li>
 *   <li>Configuration des timeouts et limites</li>
 * </ul>
 * 
 * <p><strong>Types d'emails envoyés :</strong></p>
 * <ul>
 *   <li><strong>Vérification d'email</strong> : Confirmation lors de l'inscription</li>
 *   <li><strong>Réinitialisation de mot de passe</strong> : Lien de récupération</li>
 *   <li><strong>Notifications de course</strong> : Inscriptions, modifications, rappels</li>
 *   <li><strong>Communications administratives</strong> : Annonces importantes</li>
 * </ul>
 * 
 * <p><strong>Configuration par défaut :</strong></p>
 * <ul>
 *   <li>Serveur SMTP : Gmail (smtp.gmail.com:587)</li>
 *   <li>Sécurité : TLS activé</li>
 *   <li>Authentification : Obligatoire</li>
 *   <li>Timeout : 10 secondes</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see DatabaseConfig Pour les autres configurations système
 */
public class EmailConfig {
    
    /** Serveur SMTP par défaut (Gmail) */
    private static final String DEFAULT_SMTP_HOST = "smtp.gmail.com";
    
    /** Port SMTP par défaut pour TLS */
    private static final String DEFAULT_SMTP_PORT = "587";
    
    /** Adresse email d'expédition par défaut */
    private static final String DEFAULT_FROM_EMAIL = "noreply@colorrun.com";
    
    /** Nom d'affichage pour l'expéditeur */
    private static final String DEFAULT_FROM_NAME = "Color Run";
    
    /** Timeout par défaut pour les connexions SMTP (en millisecondes) */
    private static final int DEFAULT_TIMEOUT = 10000;
    
    /** Instance singleton de la configuration email */
    private static EmailConfig instance;
    
    /** Propriétés de configuration SMTP */
    private Properties properties;
    
    /** Nom d'utilisateur pour l'authentification SMTP */
    private String username;
    
    /** Mot de passe pour l'authentification SMTP */
    private String password;
    
    /** Adresse email d'expédition */
    private String fromEmail;
    
    /** Nom d'affichage de l'expéditeur */
    private String fromName;
    
    /** Indique si le service email est activé */
    private boolean enabled;

    /**
     * Constructeur privé pour le pattern Singleton.
     * Initialise la configuration avec les valeurs par défaut.
     */
    private EmailConfig() {
        this.enabled = true;
        this.fromEmail = DEFAULT_FROM_EMAIL;
        this.fromName = DEFAULT_FROM_NAME;
        initializeDefaultProperties();
        loadConfiguration();
    }

    /**
     * Retourne l'instance unique de la configuration email (Singleton).
     * 
     * @return L'instance de configuration email
     */
    public static synchronized EmailConfig getInstance() {
        if (instance == null) {
            instance = new EmailConfig();
        }
        return instance;
    }

    /**
     * Initialise les propriétés SMTP par défaut.
     * Configure les paramètres de base pour une connexion Gmail sécurisée.
     */
    private void initializeDefaultProperties() {
        properties = new Properties();
        
        // Configuration du serveur SMTP
        properties.put("mail.smtp.host", DEFAULT_SMTP_HOST);
        properties.put("mail.smtp.port", DEFAULT_SMTP_PORT);
        
        // Activation de l'authentification
        properties.put("mail.smtp.auth", "true");
        
        // Configuration TLS pour la sécurité
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        
        // Configuration SSL comme fallback
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        // Timeouts
        properties.put("mail.smtp.connectiontimeout", String.valueOf(DEFAULT_TIMEOUT));
        properties.put("mail.smtp.timeout", String.valueOf(DEFAULT_TIMEOUT));
        properties.put("mail.smtp.writetimeout", String.valueOf(DEFAULT_TIMEOUT));
        
        // Configuration additionnelle
        properties.put("mail.debug", "false");
        properties.put("mail.transport.protocol", "smtp");
    }

    /**
     * Charge la configuration depuis les variables d'environnement ou fichier de config.
     * Permet de surcharger les valeurs par défaut avec des paramètres personnalisés.
     */
    private void loadConfiguration() {
        // Chargement depuis les variables d'environnement
        String envHost = System.getProperty("colorrun.email.smtp.host");
        if (envHost != null && !envHost.isEmpty()) {
            properties.setProperty("mail.smtp.host", envHost);
        }
        
        String envPort = System.getProperty("colorrun.email.smtp.port");
        if (envPort != null && !envPort.isEmpty()) {
            properties.setProperty("mail.smtp.port", envPort);
        }
        
        this.username = System.getProperty("colorrun.email.username");
        this.password = System.getProperty("colorrun.email.password");
        
        String envFromEmail = System.getProperty("colorrun.email.from");
        if (envFromEmail != null && !envFromEmail.isEmpty()) {
            this.fromEmail = envFromEmail;
        }
        
        String envFromName = System.getProperty("colorrun.email.fromName");
        if (envFromName != null && !envFromName.isEmpty()) {
            this.fromName = envFromName;
        }
        
        String envEnabled = System.getProperty("colorrun.email.enabled");
        if (envEnabled != null) {
            this.enabled = Boolean.parseBoolean(envEnabled);
        }
    }

    /**
     * Retourne les propriétés SMTP configurées.
     * 
     * @return Les propriétés SMTP pour JavaMail
     */
    public Properties getProperties() {
        return (Properties) properties.clone();
    }

    /**
     * Retourne le nom d'utilisateur pour l'authentification SMTP.
     * 
     * @return Le nom d'utilisateur SMTP
     */
    public String getUsername() {
        return username;
    }

    /**
     * Définit le nom d'utilisateur pour l'authentification SMTP.
     * 
     * @param username Le nom d'utilisateur SMTP
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retourne le mot de passe pour l'authentification SMTP.
     * 
     * @return Le mot de passe SMTP
     */
    public String getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe pour l'authentification SMTP.
     * 
     * @param password Le mot de passe SMTP
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retourne l'adresse email d'expédition.
     * 
     * @return L'adresse email d'expédition
     */
    public String getFromEmail() {
        return fromEmail;
    }

    /**
     * Définit l'adresse email d'expédition.
     * 
     * @param fromEmail L'adresse email d'expédition
     */
    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    /**
     * Retourne le nom d'affichage de l'expéditeur.
     * 
     * @return Le nom d'affichage de l'expéditeur
     */
    public String getFromName() {
        return fromName;
    }

    /**
     * Définit le nom d'affichage de l'expéditeur.
     * 
     * @param fromName Le nom d'affichage de l'expéditeur
     */
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    /**
     * Indique si le service email est activé.
     * 
     * @return true si le service email est activé, false sinon
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Active ou désactive le service email.
     * 
     * @param enabled true pour activer, false pour désactiver
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Retourne l'adresse email complète avec nom d'affichage.
     * Format : "Nom d'affichage <email@domain.com>"
     * 
     * @return L'adresse email formatée avec nom d'affichage
     */
    public String getFormattedFromAddress() {
        if (fromName != null && !fromName.isEmpty()) {
            return fromName + " <" + fromEmail + ">";
        }
        return fromEmail;
    }

    /**
     * Retourne le serveur SMTP configuré.
     * 
     * @return L'adresse du serveur SMTP
     */
    public String getSmtpHost() {
        return properties.getProperty("mail.smtp.host");
    }

    /**
     * Retourne le port SMTP configuré.
     * 
     * @return Le port SMTP
     */
    public int getSmtpPort() {
        return Integer.parseInt(properties.getProperty("mail.smtp.port"));
    }

    /**
     * Vérifie si l'authentification SMTP est configurée.
     * 
     * @return true si username et password sont définis, false sinon
     */
    public boolean isAuthenticationConfigured() {
        return username != null && !username.isEmpty() && 
               password != null && !password.isEmpty();
    }

    /**
     * Vérifie si TLS est activé.
     * 
     * @return true si TLS est activé, false sinon
     */
    public boolean isTlsEnabled() {
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.starttls.enable"));
    }

    /**
     * Teste la configuration email en tentant une connexion.
     * 
     * @return true si la connexion réussit, false sinon
     */
    public boolean testConnection() {
        if (!enabled || !isAuthenticationConfigured()) {
            return false;
        }
        
        try {
            // Tentative de création d'une session pour tester la configuration
            javax.mail.Session session = javax.mail.Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });
            
            // Test de transport
            javax.mail.Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du test de connexion email : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Log la configuration actuelle au démarrage
     */
    private void logConfiguration() {
        System.out.println("\n🔧 CONFIGURATION EMAIL COLOR RUN");
        System.out.println("=====================================");
        System.out.println("Service activé: " + (enabled ? "✅ OUI" : "❌ NON"));
        System.out.println("Serveur SMTP: " + getSmtpHost() + ":" + getSmtpPort());
        System.out.println("TLS activé: " + (isTlsEnabled() ? "✅ OUI" : "❌ NON"));
        System.out.println("Username: " + (username != null ? "✅ " + username : "❌ NON CONFIGURÉ"));
        System.out.println("Password: " + (password != null ? "✅ [CONFIGURÉ]" : "❌ NON CONFIGURÉ"));
        System.out.println("Email expéditeur: " + fromEmail);
        System.out.println("Nom expéditeur: " + fromName);
        System.out.println("Auth configurée: " + (isAuthenticationConfigured() ? "✅ OUI" : "❌ NON"));
        System.out.println("=====================================\n");
        
        if (!enabled) {
            System.out.println("⚠️  ATTENTION: Service email DÉSACTIVÉ");
            System.out.println("   Pour l'activer: -Dcolorrun.email.enabled=true\n");
        } else if (!isAuthenticationConfigured()) {
            System.out.println("⚠️  ATTENTION: Authentification SMTP NON CONFIGURÉE");
            System.out.println("   Configurez: -Dcolorrun.email.username=... -Dcolorrun.email.password=...\n");
        } else {
            System.out.println("✅ Configuration email prête !\n");
        }
    }

    /**
     * Recharge la configuration depuis les sources externes.
     * Utile pour actualiser la configuration sans redémarrage.
     */
    public void reloadConfiguration() {
        loadConfiguration();
    }

    /**
     * Retourne un résumé de la configuration email actuelle.
     * Utile pour le débogage et la vérification de la configuration.
     * 
     * @return Un résumé de la configuration (sans le mot de passe)
     */
    public String getConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Configuration Email:\n");
        summary.append("  - Activé: ").append(enabled).append("\n");
        summary.append("  - Serveur SMTP: ").append(getSmtpHost()).append(":").append(getSmtpPort()).append("\n");
        summary.append("  - TLS: ").append(isTlsEnabled()).append("\n");
        summary.append("  - Authentification: ").append(isAuthenticationConfigured()).append("\n");
        summary.append("  - Utilisateur: ").append(username != null ? username : "Non configuré").append("\n");
        summary.append("  - Email expéditeur: ").append(getFormattedFromAddress()).append("\n");
        
        return summary.toString();
    }

    /**
     * Retourne une représentation textuelle de la configuration.
     * 
     * @return Une chaîne décrivant la configuration email
     */
    @Override
    public String toString() {
        return "EmailConfig{" +
                "enabled=" + enabled +
                ", smtpHost='" + getSmtpHost() + '\'' +
                ", smtpPort=" + getSmtpPort() +
                ", fromEmail='" + fromEmail + '\'' +
                ", fromName='" + fromName + '\'' +
                ", authConfigured=" + isAuthenticationConfigured() +
                ", tlsEnabled=" + isTlsEnabled() +
                '}';
    }
}
