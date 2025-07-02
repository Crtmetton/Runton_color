package com.colorrun.dao;

import com.colorrun.business.Dossard;
import com.colorrun.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table DOSSARD.
 * <p>
 * Gère la création, la mise à jour et la recherche des {@link com.colorrun.business.Dossard}
 * ainsi que des requêtes spécialisées (numéro suivant disponible, dossards par course, etc.).
 * </p>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class DossardDAO {
    
    public DossardDAO() {
        // DatabaseConfig est statique, pas besoin d'instance
    }
    
    /**
     * Crée la table Dossard si elle n'existe pas.
     */
    public void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS Dossard (
                id INT PRIMARY KEY AUTO_INCREMENT,
                number INT NOT NULL,
                courseId INT NOT NULL,
                participantId INT,
                status VARCHAR(50) NOT NULL DEFAULT 'RESERVED',
                raceTime BIGINT,
                finishPosition INT,
                collected BOOLEAN DEFAULT FALSE,
                qrCodeData TEXT,
                pdfPath TEXT,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (courseId) REFERENCES Course(id) ON DELETE CASCADE,
                FOREIGN KEY (participantId) REFERENCES Utilisateur(id) ON DELETE SET NULL,
                UNIQUE KEY unique_course_number (courseId, number),
                UNIQUE KEY unique_participant_course (participantId, courseId)
            )
            """;
        
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
    
    /**
     * Sauvegarde un nouveau dossard.
     */
    public Dossard save(Dossard dossard) throws SQLException {
        String sql = """
            INSERT INTO Dossard (number, courseId, participantId, status, raceTime, 
                               finishPosition, collected, qrCodeData, pdfPath) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, dossard.getNumber());
            statement.setInt(2, dossard.getCourseId());
            
            if (dossard.getParticipantId() != null) {
                statement.setInt(3, dossard.getParticipantId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            
            statement.setString(4, dossard.getStatus());
            
            if (dossard.getRaceTime() != null) {
                statement.setLong(5, dossard.getRaceTime());
            } else {
                statement.setNull(5, Types.BIGINT);
            }
            
            if (dossard.getFinishPosition() != null) {
                statement.setInt(6, dossard.getFinishPosition());
            } else {
                statement.setNull(6, Types.INTEGER);
            }
            
            statement.setBoolean(7, dossard.isCollected());
            statement.setString(8, null); // qrCodeData sera mis à jour séparément
            statement.setString(9, null); // pdfPath sera mis à jour séparément
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du dossard, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    dossard.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création du dossard, aucun ID généré.");
                }
            }
        }
        
        return dossard;
    }
    
    /**
     * Met à jour un dossard existant.
     */
    public void update(Dossard dossard) throws SQLException {
        String sql = """
            UPDATE Dossard 
            SET number = ?, courseId = ?, participantId = ?, status = ?, 
                raceTime = ?, finishPosition = ?, collected = ?, qrCodeData = ?, pdfPath = ?
            WHERE id = ?
            """;
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, dossard.getNumber());
            statement.setInt(2, dossard.getCourseId());
            
            if (dossard.getParticipantId() != null) {
                statement.setInt(3, dossard.getParticipantId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            
            statement.setString(4, dossard.getStatus());
            
            if (dossard.getRaceTime() != null) {
                statement.setLong(5, dossard.getRaceTime());
            } else {
                statement.setNull(5, Types.BIGINT);
            }
            
            if (dossard.getFinishPosition() != null) {
                statement.setInt(6, dossard.getFinishPosition());
            } else {
                statement.setNull(6, Types.INTEGER);
            }
            
            statement.setBoolean(7, dossard.isCollected());
            statement.setString(8, null); // qrCodeData
            statement.setString(9, null); // pdfPath
            statement.setInt(10, dossard.getId());
            
            statement.executeUpdate();
        }
    }
    
    /**
     * Trouve un dossard par son ID.
     */
    public Dossard findById(int id) throws SQLException {
        String sql = "SELECT * FROM Dossard WHERE id = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDossard(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Trouve un dossard par numéro et course.
     */
    public Dossard findByNumberAndCourse(int number, int courseId) throws SQLException {
        String sql = "SELECT * FROM Dossard WHERE number = ? AND courseId = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, number);
            statement.setInt(2, courseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDossard(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Trouve un dossard par participant et course.
     */
    public Dossard findByParticipantAndCourse(int participantId, int courseId) throws SQLException {
        String sql = "SELECT * FROM Dossard WHERE participantId = ? AND courseId = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, participantId);
            statement.setInt(2, courseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDossard(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Trouve tous les dossards d'une course.
     */
    public List<Dossard> findByCourse(int courseId) throws SQLException {
        List<Dossard> dossards = new ArrayList<>();
        String sql = "SELECT * FROM Dossard WHERE courseId = ? ORDER BY number";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, courseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    dossards.add(mapResultSetToDossard(resultSet));
                }
            }
        }
        
        return dossards;
    }
    
    /**
     * Trouve le prochain numéro de dossard disponible pour une course.
     */
    public int findNextAvailableNumber(int courseId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(number), 0) + 1 FROM Dossard WHERE courseId = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, courseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 1; // Premier dossard
    }
    
    /**
     * Compte le nombre de dossards attribués pour une course.
     */
    public int countAssignedDossards(int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Dossard WHERE courseId = ? AND status IN ('ASSIGNED', 'USED')";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, courseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Supprime un dossard.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Dossard WHERE id = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
    
    /**
     * Supprime tous les dossards d'une course.
     */
    public void deleteByCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM Dossard WHERE courseId = ?";
        
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, courseId);
            statement.executeUpdate();
        }
    }
    
    /**
     * Mappe un ResultSet vers un objet Dossard.
     */
    private Dossard mapResultSetToDossard(ResultSet resultSet) throws SQLException {
        Dossard dossard = new Dossard();
        
        dossard.setId(resultSet.getInt("id"));
        dossard.setNumber(resultSet.getInt("number"));
        dossard.setCourseId(resultSet.getInt("courseId"));
        
        int participantId = resultSet.getInt("participantId");
        if (!resultSet.wasNull()) {
            dossard.setParticipantId(participantId);
        }
        
        dossard.setStatus(resultSet.getString("status"));
        
        long raceTime = resultSet.getLong("raceTime");
        if (!resultSet.wasNull()) {
            dossard.setRaceTime(raceTime);
        }
        
        int finishPosition = resultSet.getInt("finishPosition");
        if (!resultSet.wasNull()) {
            dossard.setFinishPosition(finishPosition);
        }
        
        dossard.setCollected(resultSet.getBoolean("collected"));
        
        return dossard;
    }
} 