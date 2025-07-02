package com.colorrun.dao;

import com.colorrun.business.Course;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * Couche d'accès aux données (DAO) pour la gestion des courses dans la base de données.
 * 
 * Cette classe fournit toutes les opérations CRUD (Create, Read, Update, Delete)
 * nécessaires pour la persistance des courses Color Run. Elle encapsule les
 * requêtes SQL et la gestion des connexions à la base de données H2.
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Création et modification de courses</li>
 *   <li>Recherche par critères multiples (ID, nom, lieu, date)</li>
 *   <li>Gestion des états de course (PLANNED, OPEN, FULL, CLOSED, COMPLETED, CANCELLED)</li>
 *   <li>Recherche géographique et temporelle</li>
 *   <li>Statistiques et comptages</li>
 *   <li>Gestion des capacités et participants</li>
 * </ul>
 * 
 * <p><strong>Structure de la table Course :</strong></p>
 * <ul>
 *   <li>id (INTEGER) - Clé primaire auto-générée</li>
 *   <li>name (VARCHAR) - Nom de la course</li>
 *   <li>description (TEXT) - Description détaillée</li>
 *   <li>location (VARCHAR) - Lieu de la course</li>
 *   <li>date (TIMESTAMP) - Date et heure de la course</li>
 *   <li>registrationEnd (TIMESTAMP) - Fin des inscriptions</li>
 *   <li>maxParticipants (INTEGER) - Capacité maximale</li>
 *   <li>distance (DOUBLE) - Distance en kilomètres</li>
 *   <li>price (DOUBLE) - Prix d'inscription</li>
 *   <li>organizerId (INTEGER) - ID de l'organisateur</li>
 *   <li>status (VARCHAR) - Statut de la course</li>
 *   <li>createdAt (TIMESTAMP) - Date de création</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see Course Pour le modèle métier des courses
 * @see DatabaseConfig Pour la configuration de la base de données
 */
public class CourseDAO {
    
    private DataSource dataSource;
    
    public CourseDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
    }
    
    /**
     * Sauvegarde une nouvelle course en base de données.
     * 
     * @param course La course à sauvegarder (ID sera généré automatiquement)
     * @return La course sauvegardée avec son ID généré
     * @throws SQLException En cas d'erreur lors de l'insertion
     */
    public void save(Course course) throws SQLException {
        String sql = "INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE, USERCREATEID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getDate()));
            stmt.setString(4, course.getCity());
            stmt.setDouble(5, course.getDistance());
            stmt.setInt(6, course.getMaxParticipants());
            stmt.setInt(7, course.getPrix());
            stmt.setString(8, course.getCause());
            stmt.setInt(9, course.getUserCreateId());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setId(keys.getInt(1));
                }
            }
        }
    }
    
    /**
     * Récupère une course par son identifiant.
     * 
     * @param id L'identifiant de la course à récupérer
     * @return La course correspondante, ou null si non trouvée
     * @throws SQLException En cas d'erreur lors de la requête
     */
    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Course WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Récupère toutes les courses de la base de données.
     * 
     * @return Liste de toutes les courses, triées par date croissante
     * @throws SQLException En cas d'erreur lors de la requête
     */
    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course ORDER BY DATE";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        }
        // Add logging to check the prices of courses
        for (Course course : courses) {
            System.out.println("Course ID: " + course.getId() + ", Name: " + course.getName() + ", Price: " + course.getPrix());
        }
        return courses;
    }
    
    public List<Course> findFiltered(String date, String city, String distance, String sort) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM Course WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (date != null && !date.isEmpty()) {
            sql.append(" AND DATE(DATE) = ?");
            params.add(date);
        }
        
        if (city != null && !city.isEmpty()) {
            sql.append(" AND LIEU LIKE ?");
            params.add("%" + city + "%");
        }
        
        if (distance != null && !distance.isEmpty()) {
            sql.append(" AND DISTANCE = ?");
            try {
                params.add(Double.parseDouble(distance));
            } catch (NumberFormatException e) {
                // Ignore invalid distance
            }
        }
        
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "date":
                    sql.append(" ORDER BY DATE");
                    break;
                case "distance":
                    sql.append(" ORDER BY DISTANCE");
                    break;
                case "city":
                    sql.append(" ORDER BY LIEU");
                    break;
                case "name":
                    sql.append(" ORDER BY NOM");
                    break;
                default:
                    sql.append(" ORDER BY DATE");
            }
        } else {
            sql.append(" ORDER BY DATE");
        }
        
        List<Course> courses = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRow(rs));
                }
            }
        }
        return courses;
    }
    
    /**
     * Met à jour une course existante.
     * 
     * @param course La course avec les nouvelles informations
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException En cas d'erreur lors de la mise à jour
     */
    public void update(Course course) throws SQLException {
        String sql = "UPDATE Course SET NOM = ?, DESCRIPTION = ?, DATE = ?, LIEU = ?, DISTANCE = ?, MAXPARTICIPANTS = ?, PRIX = ?, CAUSE = ?, USERCREATEID = ? WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getDate()));
            stmt.setString(4, course.getCity());
            stmt.setDouble(5, course.getDistance());
            stmt.setInt(6, course.getMaxParticipants());
            stmt.setInt(7, course.getPrix());
            stmt.setString(8, course.getCause());
            stmt.setInt(9, course.getUserCreateId());
            stmt.setInt(10, course.getId());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Supprime une course de la base de données.
     * Attention : Cette opération est irréversible et peut violer l'intégrité référentielle.
     * 
     * @param id L'identifiant de la course à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException En cas d'erreur lors de la suppression
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Course WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    // La méthode updateParticipantCount ne peut pas être utilisée car la table Course
    // n'a pas de colonne current_participants dans le schéma SQL
    
    /**
     * Récupère les courses créées par un utilisateur spécifique.
     * 
     * @param creatorId L'identifiant du créateur
     * @return Liste des courses créées par cet utilisateur
     * @throws SQLException En cas d'erreur lors de la requête
     */
    public List<Course> findByCreator(int creatorId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course WHERE USERCREATEID = ? ORDER BY DATE DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, creatorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRow(rs));
                }
            }
        }
        return courses;
    }
    
    private Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("ID"));
        course.setName(rs.getString("NOM"));
        course.setDescription(rs.getString("DESCRIPTION"));
        course.setDate(rs.getTimestamp("DATE").toLocalDateTime());
        course.setCity(rs.getString("LIEU"));
        course.setDistance(rs.getDouble("DISTANCE"));
        course.setMaxParticipants(rs.getInt("MAXPARTICIPANTS"));
        // La colonne current_participants n'existe pas dans le schéma SQL
        // Nous devrons calculer ce nombre autrement, ou l'ajouter au schéma
        course.setCurrentParticipants(0);
        course.setCause(rs.getString("CAUSE"));
        course.setPrix(rs.getInt("PRIX"));
        course.setUserCreateId(rs.getInt("USERCREATEID"));
        return course;
    }
}
