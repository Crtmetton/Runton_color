package com.colorrun.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Configuration de la base de données pour l'application Color Run.
 * 
 * Cette classe gère la configuration et l'initialisation de la source de données
 * pour la base de données H2. Elle fournit un accès centralisé aux connexions
 * de base de données et assure une configuration uniforme à travers l'application.
 * 
 * <p><strong>Caractéristiques de la base de données :</strong></p>
 * <ul>
 *   <li>Type : H2 Database (base de données embarquée)</li>
 *   <li>Mode : Fichier avec serveur automatique</li>
 *   <li>Emplacement : ./dbFiles/Runton_color_Prod</li>
 *   <li>Utilisateur : Runton</li>
 *   <li>Mot de passe : (vide)</li>
 * </ul>
 * 
 * <p><strong>Mode AUTO_SERVER :</strong></p>
 * Le mode AUTO_SERVER permet à plusieurs connexions d'accéder simultanément
 * à la même base de données, ce qui est utile pour les outils d'administration
 * et le développement.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see DataSource Pour la source de données JDBC
 * @see Connection Pour les connexions à la base de données
 */
public class DatabaseConfig {
    
    /** URL de connexion à la base de données H2 */
    private static final String DB_URL = "jdbc:h2:file:./dbFiles/Runton_color_Prod;AUTO_SERVER=TRUE";
    
    /** Nom d'utilisateur pour la connexion à la base de données */
    private static final String DB_USER = "Runton";
    
    /** Mot de passe pour la connexion à la base de données (vide par défaut) */
    private static final String DB_PASSWORD = "";
    
    /** Source de données partagée pour toute l'application */
    private static DataSource dataSource;
    
    /**
     * Bloc d'initialisation statique.
     * 
     * Ce bloc s'exécute lors du premier chargement de la classe et initialise
     * automatiquement la source de données. En cas d'erreur, une RuntimeException
     * est levée pour signaler un problème de configuration critique.
     */
    static {
        try {
            initDataSource();
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'initialisation de la base de données", e);
        }
    }
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     * 
     * Cette classe est conçue pour être utilisée de manière statique uniquement.
     */
    private DatabaseConfig() {
        // Classe utilitaire - ne doit pas être instanciée
    }
    
    /**
     * Initialise la source de données H2.
     * 
     * Cette méthode configure une source de données JDBC pour H2 avec les
     * paramètres de connexion définis. Elle est appelée automatiquement
     * lors du chargement de la classe.
     * 
     * @throws RuntimeException Si l'initialisation échoue
     */
    private static void initDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(DB_USER);
        ds.setPassword(DB_PASSWORD);
        dataSource = ds;
    }
    
    /**
     * Retourne la source de données configurée.
     * 
     * Cette méthode fournit un accès à la source de données JDBC configurée
     * pour l'application. La source de données est partagée et thread-safe.
     * 
     * @return La source de données JDBC pour la base H2
     * 
     * @example
     * DataSource ds = DatabaseConfig.getDataSource();
     * try (Connection conn = ds.getConnection()) {
     *     // Utiliser la connexion
     * }
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * Obtient une nouvelle connexion à la base de données.
     * 
     * Cette méthode est un raccourci pour obtenir directement une connexion
     * sans passer par la DataSource. La connexion doit être fermée après usage
     * dans un bloc try-with-resources pour éviter les fuites de ressources.
     * 
     * @return Une nouvelle connexion à la base de données
     * @throws SQLException Si la connexion ne peut pas être établie
     * 
     * @example
     * try (Connection conn = DatabaseConfig.getConnection()) {
     *     PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Utilisateur");
     *     // Exécuter la requête
     * } catch (SQLException e) {
     *     // Gérer l'erreur
     * }
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * Retourne les informations de configuration de la base de données.
     * 
     * Cette méthode est utile pour le débogage et la vérification de la
     * configuration. Elle retourne les paramètres de connexion (sans le mot de passe).
     * 
     * @return Une chaîne contenant les informations de configuration
     */
    public static String getConfigInfo() {
        return "Base de données H2 - URL: " + DB_URL + ", Utilisateur: " + DB_USER;
    }
    
    /**
     * Teste la connectivité à la base de données.
     * 
     * Cette méthode vérifie que la connexion à la base de données peut être
     * établie et que celle-ci répond correctement. Utile pour les vérifications
     * de santé de l'application.
     * 
     * @return true si la connexion est réussie, false sinon
     * 
     * @example
     * if (DatabaseConfig.testConnection()) {
     *     System.out.println("Base de données accessible");
     * } else {
     *     System.err.println("Problème de connexion à la base");
     * }
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            // Test simple : exécuter une requête de vérification
            return conn.isValid(5); // Timeout de 5 secondes
        } catch (SQLException e) {
            return false;
        }
    }
}
