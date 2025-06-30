package com.colorrun.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaire de logging pour Color Run.
 * Améliore les logs console avec timestamps et codes couleur.
 */
public class Logger {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Codes couleur ANSI pour terminal
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";
    
    /**
     * Log d'information (bleu)
     */
    public static void info(String component, String message) {
        log("INFO", BLUE, component, message);
    }
    
    /**
     * Log d'information (bleu) avec objet
     */
    public static void info(String component, String message, Object object) {
        log("INFO", BLUE, component, message + " | Data: " + object);
    }
    
    /**
     * Log de succès (vert)
     */
    public static void success(String component, String message) {
        log("SUCCESS", GREEN, component, message);
    }
    
    /**
     * Log d'avertissement (jaune)
     */
    public static void warn(String component, String message) {
        log("WARN", YELLOW, component, message);
    }
    
    /**
     * Log d'erreur (rouge)
     */
    public static void error(String component, String message) {
        log("ERROR", RED, component, message);
    }
    
    /**
     * Log d'erreur avec exception (rouge)
     */
    public static void error(String component, String message, Exception e) {
        log("ERROR", RED, component, message + " | Exception: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        if (e.getCause() != null) {
            log("ERROR", RED, component, "Cause: " + e.getCause().getMessage());
        }
    }
    
    /**
     * Log de debug (purple)
     */
    public static void debug(String component, String message) {
        log("DEBUG", PURPLE, component, message);
    }
    
    /**
     * Log d'email (cyan)
     */
    public static void email(String component, String message) {
        log("EMAIL", CYAN, component, message);
    }
    
    /**
     * Log d'email avec destinataire
     */
    public static void email(String component, String message, String recipient) {
        log("EMAIL", CYAN, component, message + " | To: " + recipient);
    }
    
    /**
     * Log de base de données (blanc)
     */
    public static void database(String component, String message) {
        log("DATABASE", WHITE, component, message);
    }
    
    /**
     * Log formaté avec couleur
     */
    private static void log(String level, String color, String component, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formattedMessage = String.format(
            "%s[%s]%s %s%s[%s]%s %s%s%s %s", 
            color, timestamp, RESET,
            BOLD, color, level, RESET,
            color, component, RESET,
            message
        );
        System.out.println(formattedMessage);
    }
    
    /**
     * Affiche une ligne de séparation pour les étapes importantes
     */
    public static void separator(String title) {
        System.out.println();
        System.out.println(BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(BOLD + CYAN + "  " + title + RESET);
        System.out.println(BOLD + CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println();
    }
    
    /**
     * Log de démarrage d'une étape importante
     */
    public static void step(String component, String step) {
        log("STEP", BOLD + GREEN, component, "🔄 " + step);
    }
    
    /**
     * Log de fin d'étape avec succès
     */
    public static void stepSuccess(String component, String step) {
        log("STEP", BOLD + GREEN, component, "✅ " + step + " - TERMINÉ");
    }
    
    /**
     * Log de fin d'étape avec échec
     */
    public static void stepFailed(String component, String step, String reason) {
        log("STEP", BOLD + RED, component, "❌ " + step + " - ÉCHEC: " + reason);
    }
    
    /**
     * Log de configuration
     */
    public static void config(String component, String key, String value) {
        log("CONFIG", YELLOW, component, key + " = " + (value != null ? value : "NON DÉFINI"));
    }
    
    /**
     * Log de métriques/performance
     */
    public static void metric(String component, String metric, Object value) {
        log("METRIC", PURPLE, component, metric + ": " + value);
    }
} 