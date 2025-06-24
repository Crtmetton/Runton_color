package com.colorrun.dao;

import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import com.colorrun.business.User;
import com.colorrun.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class ParticipationDAO {
    
    private DataSource dataSource;
    private CourseDAO courseDAO;
    private UserDAO userDAO;
    
    public ParticipationDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.courseDAO = new CourseDAO();
        this.userDAO = new UserDAO();
    }
    
    public void save(Participation participation) throws SQLException {
        String sql = "INSERT INTO Participation (utilisateurId, courseId, date, statut) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, participation.getUser().getId());
            stmt.setInt(2, participation.getCourse().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(participation.getDate()));
            stmt.setString(4, participation.getStatus());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    participation.setId(keys.getInt(1));
                }
            }
        }
    }
    
    public Optional<Participation> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Participation WHERE id = ?";
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
    
    public Optional<Participation> findByUserAndCourse(int userId, int courseId) throws SQLException {
        String sql = "SELECT * FROM Participation WHERE utilisateurId = ? AND courseId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    public List<Participation> findByUser(int userId) throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM Participation WHERE utilisateurId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participations.add(mapRow(rs));
                }
            }
        }
        return participations;
    }
    
    public List<Participation> findByCourse(int courseId) throws SQLException {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM Participation WHERE courseId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participations.add(mapRow(rs));
                }
            }
        }
        return participations;
    }
    
    public int countByCourse(int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Participation WHERE courseId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public void update(Participation participation) throws SQLException {
        String sql = "UPDATE Participation SET utilisateurId = ?, courseId = ?, date = ?, statut = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participation.getUser().getId());
            stmt.setInt(2, participation.getCourse().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(participation.getDate()));
            stmt.setString(4, participation.getStatus());
            stmt.setInt(5, participation.getId());
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Participation WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    private Participation mapRow(ResultSet rs) throws SQLException {
        Participation participation = new Participation();
        participation.setId(rs.getInt("id"));
        
        int userId = rs.getInt("utilisateurId");
        User user = userDAO.findById(userId).orElse(new User());
        participation.setUser(user);
        
        int courseId = rs.getInt("courseId");
        Course course = courseDAO.findById(courseId).orElse(new Course());
        participation.setCourse(course);
        
        participation.setDate(rs.getTimestamp("date").toLocalDateTime());
        participation.setStatus(rs.getString("statut"));
        
        return participation;
    }
}
