package com.colorrun.service.impl;

import com.colorrun.business.Course;
import com.colorrun.dao.CourseDAO;
import com.colorrun.dao.ParticipationDAO;
import com.colorrun.service.CourseService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CourseServiceImpl implements CourseService {
    
    private CourseDAO courseDAO;
    private ParticipationDAO participationDAO;
    
    public CourseServiceImpl() {
        this.courseDAO = new CourseDAO();
        this.participationDAO = new ParticipationDAO();
    }
    
    @Override
    public void createCourse(Course course) throws SQLException {
        course.setCurrentParticipants(0); // Initialiser le nombre de participants à 0
        courseDAO.save(course);
    }
    
    @Override
    public Course getCourseById(int id) throws SQLException {
        Optional<Course> course = courseDAO.findById(id);
        if (!course.isPresent()) {
            throw new SQLException("Course not found with ID: " + id);
        }
        
        // Mettre à jour le nombre actuel de participants
        int participantCount = participationDAO.countByCourse(id);
        Course foundCourse = course.get();
        foundCourse.setCurrentParticipants(participantCount);
        
        return foundCourse;
    }
    
    @Override
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = courseDAO.findAll();
        
        // Mettre à jour le nombre actuel de participants pour chaque course
        for (Course course : courses) {
            int participantCount = participationDAO.countByCourse(course.getId());
            course.setCurrentParticipants(participantCount);
        }
        
        return courses;
    }
    
    @Override
    public List<Course> getFilteredCourses(String date, String city, String distance, String sort) throws SQLException {
        List<Course> courses = courseDAO.findFiltered(date, city, distance, sort);
        
        // Mettre à jour le nombre actuel de participants pour chaque course
        for (Course course : courses) {
            int participantCount = participationDAO.countByCourse(course.getId());
            course.setCurrentParticipants(participantCount);
        }
        
        return courses;
    }
    
    @Override
    public void updateCourse(Course course) throws SQLException {
        courseDAO.update(course);
    }
    
    @Override
    public void deleteCourse(int id) throws SQLException {
        courseDAO.delete(id);
    }
    
    // Méthodes de compatibilité avec les servlets existants
    
    @Override
    public void create(Course course) throws SQLException {
        createCourse(course);
    }
    
    @Override
    public Optional<Course> findById(int id) throws SQLException {
        try {
            Course course = getCourseById(id);
            return Optional.of(course);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Course> findAll() throws SQLException {
        return getAllCourses();
    }
    
    @Override
    public List<Course> findFiltered(String date, String city, String distance, String sort) throws SQLException {
        return getFilteredCourses(date, city, distance, sort);
    }
    
    @Override
    public void update(Course course) throws SQLException {
        updateCourse(course);
    }
    
    @Override
    public void delete(int id) throws SQLException {
        deleteCourse(id);
    }
    
    @Override
    public boolean hasAvailableSpots(int courseId) throws SQLException {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            return false;
        }
        
        Course course = courseOpt.get();
        int currentParticipants = participationDAO.countByCourse(courseId);
        
        return currentParticipants < course.getMaxParticipants();
    }
} 