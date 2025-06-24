package com.colorrun.service;

import com.colorrun.business.Course;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    
    void createCourse(Course course) throws SQLException;
    
    Course getCourseById(int id) throws SQLException;
    
    List<Course> getAllCourses() throws SQLException;
    
    List<Course> getFilteredCourses(String date, String city, String distance, String sort) throws SQLException;
    
    void updateCourse(Course course) throws SQLException;
    
    void deleteCourse(int id) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    void create(Course course) throws SQLException;
    
    Optional<Course> findById(int id) throws SQLException;
    
    List<Course> findAll() throws SQLException;
    
    List<Course> findFiltered(String date, String city, String distance, String sort) throws SQLException;
    
    void update(Course course) throws SQLException;
    
    void delete(int id) throws SQLException;
    
    boolean hasAvailableSpots(int courseId) throws SQLException;
} 