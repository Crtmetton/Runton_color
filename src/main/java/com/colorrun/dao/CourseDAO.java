package com.colorrun.dao;

import com.colorrun.business.Course;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class CourseDAO {
    
    private DataSource dataSource;
    
    public CourseDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
    }
    
    public void save(Course course) throws SQLException {
        String sql = "INSERT INTO Course (nom, description, date, lieu, distance, maxParticipants, prix, cause) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getDate()));
            stmt.setString(4, course.getCity());
            stmt.setDouble(5, course.getDistance());
            stmt.setInt(6, course.getMaxParticipants());
            stmt.setInt(7, 0); // prix est requis mais n'existe pas dans l'objet Course
            stmt.setString(8, course.getCause());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setId(keys.getInt(1));
                }
            }
        }
    }
    
    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Course WHERE id = ?";
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
    
    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course ORDER BY date";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        }
        return courses;
    }
    
    public List<Course> findFiltered(String date, String city, String distance, String sort) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM Course WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (date != null && !date.isEmpty()) {
            sql.append(" AND DATE(date) = ?");
            params.add(date);
        }
        
        if (city != null && !city.isEmpty()) {
            sql.append(" AND lieu LIKE ?");
            params.add("%" + city + "%");
        }
        
        if (distance != null && !distance.isEmpty()) {
            sql.append(" AND distance = ?");
            try {
                params.add(Double.parseDouble(distance));
            } catch (NumberFormatException e) {
                // Ignore invalid distance
            }
        }
        
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "date":
                    sql.append(" ORDER BY date");
                    break;
                case "distance":
                    sql.append(" ORDER BY distance");
                    break;
                case "city":
                    sql.append(" ORDER BY lieu");
                    break;
                default:
                    sql.append(" ORDER BY date");
            }
        } else {
            sql.append(" ORDER BY date");
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
    
    public void update(Course course) throws SQLException {
        String sql = "UPDATE Course SET nom = ?, description = ?, date = ?, lieu = ?, distance = ?, maxParticipants = ?, cause = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(course.getDate()));
            stmt.setString(4, course.getCity());
            stmt.setDouble(5, course.getDistance());
            stmt.setInt(6, course.getMaxParticipants());
            stmt.setString(7, course.getCause());
            stmt.setInt(8, course.getId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Course WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    // La méthode updateParticipantCount ne peut pas être utilisée car la table Course
    // n'a pas de colonne current_participants dans le schéma SQL
    
    private Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("nom"));
        course.setDescription(rs.getString("description"));
        course.setDate(rs.getTimestamp("date").toLocalDateTime());
        course.setCity(rs.getString("lieu"));
        course.setDistance(rs.getDouble("distance"));
        course.setMaxParticipants(rs.getInt("maxParticipants"));
        // La colonne current_participants n'existe pas dans le schéma SQL
        // Nous devrons calculer ce nombre autrement, ou l'ajouter au schéma
        course.setCurrentParticipants(0);
        course.setCause(rs.getString("cause"));
        return course;
    }
}
