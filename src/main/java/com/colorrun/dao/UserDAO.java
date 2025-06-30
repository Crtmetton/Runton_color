package com.colorrun.dao;

import com.colorrun.business.User;
import com.colorrun.config.DatabaseConfig;
import com.colorrun.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) pour la gestion des utilisateurs en base de données.
 * 
 * Cette classe encapsule toutes les opérations d'accès aux données relatives aux utilisateurs
 * dans la base de données H2. Elle fournit une abstraction de la couche de persistance
 * et isole la logique SQL du reste de l'application.
 * 
 * <p><strong>Responsabilités :</strong></p>
 * <ul>
 *   <li>Opérations CRUD sur la table Utilisateur</li>
 *   <li>Recherches par critères (email, rôle, ID)</li>
 *   <li>Gestion des mots de passe avec hachage sécurisé</li>
 *   <li>Validation de l'unicité des emails</li>
 *   <li>Gestion des connexions et transactions</li>
 * </ul>
 * 
 * <p><strong>Structure de la table Utilisateur :</strong></p>
 * <ul>
 *   <li><code>id</code> : Clé primaire auto-incrémentée</li>
 *   <li><code>prenom</code> : Prénom de l'utilisateur</li>
 *   <li><code>nom</code> : Nom de famille</li>
 *   <li><code>email</code> : Email unique</li>
 *   <li><code>motDePasse</code> : Hash BCrypt du mot de passe</li>
 *   <li><code>role</code> : Rôle de l'utilisateur (ADMIN, ORGANIZER, PARTICIPANT)</li>
 *   <li><code>actif</code> : Statut d'activation du compte</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see User Pour le modèle de données utilisateur
 * @see DatabaseConfig Pour la configuration de la base de données
 * @see PasswordUtil Pour le hachage sécurisé des mots de passe
 */
public class UserDAO {
    
    /**
     * Constructeur par défaut.
     * Utilise la configuration statique de base de données pour les opérations DAO.
     */
    public UserDAO() {
        // La configuration de base de données est gérée de manière statique
    }
    
    /**
     * Insère un nouvel utilisateur dans la base de données.
     * 
     * Cette méthode crée un nouveau compte utilisateur avec hachage automatique
     * du mot de passe. L'email doit être unique dans le système.
     * 
     * @param user L'utilisateur à créer (sans ID, qui sera généré automatiquement)
     * @return L'utilisateur créé avec son ID assigné par la base de données
     * @throws SQLException Si erreur lors de l'insertion ou si l'email existe déjà
     * 
     * @example
     * User newUser = new User("Jean", "Dupont", "jean@example.com", "password123", "PARTICIPANT");
     * User savedUser = userDAO.save(newUser);
     * System.out.println("Nouvel ID utilisateur : " + savedUser.getId());
     */
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Préparation des paramètres de la requête
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, PasswordUtil.hashPassword(user.getPasswordHash())); // Hachage sécurisé
            stmt.setString(5, user.getRole());
            stmt.setBoolean(6, user.isEnabled());
            
            // Exécution de l'insertion
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création de l'utilisateur, aucune ligne affectée.");
            }
            
            // Récupération de l'ID généré automatiquement
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création de l'utilisateur, aucun ID obtenu.");
                }
            }
            
            return user;
        }
    }
    
    /**
     * Recherche un utilisateur par son identifiant unique.
     * 
     * @param id L'identifiant unique de l'utilisateur
     * @return Un Optional contenant l'utilisateur trouvé, ou Optional.empty() si non trouvé
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * Optional<User> user = userDAO.findById(123);
     * if (user.isPresent()) {
     *     System.out.println("Utilisateur trouvé : " + user.get().getEmail());
     * }
     */
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Recherche un utilisateur par son adresse email.
     * 
     * L'email étant unique dans le système, cette méthode retourne
     * au maximum un utilisateur.
     * 
     * @param email L'adresse email à rechercher
     * @return Un Optional contenant l'utilisateur trouvé, ou Optional.empty() si non trouvé
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * Optional<User> user = userDAO.findByEmail("jean@example.com");
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Récupère tous les utilisateurs ayant un rôle spécifique.
     * 
     * @param role Le rôle des utilisateurs à rechercher (ADMIN, ORGANIZER, PARTICIPANT)
     * @return La liste des utilisateurs ayant ce rôle
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * List<User> organizers = userDAO.findByRole("ORGANIZER");
     * System.out.println("Nombre d'organisateurs : " + organizers.size());
     */
    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE role = ? ORDER BY nom, prenom";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Récupère tous les utilisateurs du système.
     * 
     * Cette méthode est généralement utilisée par les administrateurs
     * pour la gestion globale des comptes. Les résultats sont triés
     * par nom puis prénom.
     * 
     * @return La liste de tous les utilisateurs
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * List<User> allUsers = userDAO.findAll();
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM Utilisateur ORDER BY nom, prenom";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Met à jour les informations d'un utilisateur existant.
     * 
     * Cette méthode met à jour tous les champs de l'utilisateur sauf le mot de passe.
     * Pour changer le mot de passe, utilisez la méthode updatePassword().
     * 
     * @param user L'utilisateur avec les nouvelles informations
     * @throws SQLException Si erreur lors de la mise à jour ou si l'utilisateur n'existe pas
     * 
     * @example
     * user.setFirstName("Nouveau Prénom");
     * userDAO.update(user);
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE Utilisateur SET prenom = ?, nom = ?, email = ?, role = ?, enabled = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setBoolean(5, user.isEnabled());
            stmt.setInt(6, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID : " + user.getId());
            }
        }
    }
    
    /**
     * Met à jour le mot de passe d'un utilisateur.
     * 
     * Cette méthode hache automatiquement le nouveau mot de passe
     * avant de le stocker en base de données.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param newPassword Le nouveau mot de passe en clair
     * @throws SQLException Si erreur lors de la mise à jour ou si l'utilisateur n'existe pas
     * 
     * @example
     * userDAO.updatePassword(123, "nouveauMotDePasse123");
     */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE Utilisateur SET hashMotDePasse = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID : " + userId);
            }
        }
    }
    
    /**
     * Change le rôle d'un utilisateur.
     * 
     * Cette action est généralement réservée aux administrateurs.
     * Les rôles possibles sont : ADMIN, ORGANIZER, PARTICIPANT.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param newRole Le nouveau rôle à assigner
     * @throws SQLException Si erreur lors de la mise à jour ou si l'utilisateur n'existe pas
     * 
     * @example
     * userDAO.updateRole(123, "ORGANIZER"); // Promeut en organisateur
     */
    public void updateRole(int userId, String newRole) throws SQLException {
        String sql = "UPDATE Utilisateur SET role = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID : " + userId);
            }
        }
    }
    
    /**
     * Active ou désactive un compte utilisateur.
     * 
     * Un compte désactivé ne peut pas se connecter à l'application.
     * Cette action est généralement réservée aux administrateurs.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @param enabled true pour activer, false pour désactiver
     * @throws SQLException Si erreur lors de la mise à jour ou si l'utilisateur n'existe pas
     * 
     * @example
     * userDAO.setEnabled(123, false); // Désactive le compte
     */
    public void setEnabled(int userId, boolean enabled) throws SQLException {
        String sql = "UPDATE Utilisateur SET enabled = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, enabled);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID : " + userId);
            }
        }
    }
    
    /**
     * Supprime définitivement un utilisateur de la base de données.
     * 
     * Cette action est irréversible et supprime toutes les données
     * associées à l'utilisateur. Généralement réservée aux administrateurs.
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @throws SQLException Si erreur lors de la suppression
     * 
     * @example
     * userDAO.delete(123); // Supprime définitivement l'utilisateur
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Aucun utilisateur trouvé avec l'ID : " + id);
            }
        }
    }
    
    /**
     * Vérifie si un email est déjà utilisé par un autre utilisateur.
     * 
     * Utile lors de l'inscription ou de la modification d'email
     * pour s'assurer de l'unicité.
     * 
     * @param email L'adresse email à vérifier
     * @return true si l'email existe déjà, false sinon
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * if (!userDAO.emailExists("nouveau@example.com")) {
     *     // L'email est disponible
     * }
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Utilisateur WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Compte le nombre total d'utilisateurs dans le système.
     * 
     * Utile pour les statistiques et la gestion administrative.
     * 
     * @return Le nombre total d'utilisateurs
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * int totalUsers = userDAO.count();
     * System.out.println("Nombre d'utilisateurs : " + totalUsers);
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Utilisateur";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre d'utilisateurs ayant un rôle spécifique.
     * 
     * @param role Le rôle à compter (ADMIN, ORGANIZER, PARTICIPANT)
     * @return Le nombre d'utilisateurs ayant ce rôle
     * @throws SQLException Si erreur lors de l'accès à la base de données
     * 
     * @example
     * int organizerCount = userDAO.countByRole("ORGANIZER");
     */
    public int countByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Utilisateur WHERE role = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Convertit un ResultSet en objet User.
     * 
     * Cette méthode utilitaire privée mappe les colonnes de la base de données
     * aux propriétés de l'objet User. Elle gère la correspondance entre les
     * noms de colonnes français de la base et les attributs anglais de l'objet.
     * 
     * @param rs Le ResultSet contenant les données utilisateur
     * @return Un objet User avec les données mappées
     * @throws SQLException Si erreur lors de la lecture du ResultSet
     * 
     * <p><strong>Mapping des colonnes :</strong></p>
     * <ul>
     *   <li>id → setId()</li>
     *   <li>prenom → setFirstName()</li>
     *   <li>nom → setLastName()</li>
     *   <li>email → setEmail()</li>
     *   <li>motDePasse → setPasswordHash()</li>
     *   <li>role → setRole()</li>
     *   <li>actif → setEnabled()</li>
     * </ul>
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("prenom"));
        user.setLastName(rs.getString("nom"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("hashMotDePasse"));
        user.setRole(rs.getString("role"));
        user.setEnabled(rs.getBoolean("enabled"));
        
        return user;
    }
}
