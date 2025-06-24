package com.colorrun.service;

import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ParticipationService {
    
    void registerParticipation(int userId, int courseId) throws SQLException;
    
    List<Participation> getUserParticipations(int userId) throws SQLException;
    
    List<Participation> getCourseParticipations(int courseId) throws SQLException;
    
    boolean isUserRegistered(int userId, int courseId) throws SQLException;
    
    void cancelParticipation(int userId, int courseId) throws SQLException;
    
    List<Course> getUserCourses(int userId) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    int register(int userId, int courseId) throws SQLException;
    
    Optional<Participation> findById(int id) throws SQLException;
    
    List<Participation> findByUser(int userId) throws SQLException;
    
    List<Participation> findByCourse(int courseId) throws SQLException;
    
    List<Course> findCoursesByUser(int userId) throws SQLException;
    
    void cancel(int participationId) throws SQLException;
    
    byte[] generateBibPdf(int participationId) throws SQLException;
} 