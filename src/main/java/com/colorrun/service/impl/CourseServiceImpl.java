package com.colorrun.service.impl;

import com.colorrun.business.Course;
import com.colorrun.dao.CourseDAO;
import com.colorrun.dao.ParticipationDAO;
import com.colorrun.service.CourseService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

/**
 * Implémentation concrète de {@link com.colorrun.service.CourseService}.
 * <p>
 * Cette classe délègue la persistance aux DAO et applique la logique métier
 * minimale (à compléter). Les méthodes marquées TODO devront être enrichies
 * pour respecter toutes les règles décrites dans l'interface.
 * </p>
 */
public class CourseServiceImpl implements CourseService {
    
    private CourseDAO courseDAO;
    private ParticipationDAO participationDAO;
    
    public CourseServiceImpl() {
        this.courseDAO = new CourseDAO();
        this.participationDAO = new ParticipationDAO();
    }
    
    // Méthodes principales requises par l'interface
    
    /**
     * Valide les champs obligatoires et la cohérence des données avant
     * l'enregistrement ou la mise à jour d'une course.
     *
     * @param course course à valider
     * @throws IllegalArgumentException si une règle n'est pas respectée
     */
    @Override
    public void validateCourseData(Course course) {
        // TODO: implémenter validation
    }
    
    @Override
    public void createCourse(Course course) throws SQLException {
        course.setCurrentParticipants(0);
        courseDAO.save(course);
    }
    
    @Override
    public Course getCourseById(int id) throws SQLException {
        Optional<Course> course = courseDAO.findById(id);
        if (!course.isPresent()) {
            throw new SQLException("Course not found with ID: " + id);
        }
        return course.get();
    }
    
    @Override
    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAll();
    }
    
    public void updateCourse(Course course) throws SQLException {
        courseDAO.update(course);
    }
    
    public void deleteCourse(int id) throws SQLException {
        courseDAO.delete(id);
    }
    
    @Override
    public boolean hasAvailableSpots(int courseId) {
        try {
            Optional<Course> courseOpt = courseDAO.findById(courseId);
            if (!courseOpt.isPresent()) {
                return false;
            }
            Course course = courseOpt.get();
            int currentParticipants = participationDAO.countByCourse(courseId);
            return currentParticipants < course.getMaxParticipants();
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Implémentations minimales pour toutes les méthodes de l'interface
    
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
    public void update(Course course) throws SQLException {
        updateCourse(course);
    }
    
    @Override
    public void delete(int id) throws SQLException {
        deleteCourse(id);
    }
    
    @Override
    public boolean canUserModifyCourse(int userId, int courseId) {
        return false; // TODO: implémenter
    }
    
    @Override
    public double getFillPercentage(int courseId) {
        return 0.0; // TODO: implémenter
    }
    
    @Override
    public long getCourseCountByStatus(String status) {
        return 0; // TODO: implémenter
    }
    
    @Override
    public List<Course> findCoursesByName(String name) {
        try {
            return courseDAO.findAll(); // TODO: implémenter recherche par nom
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Course> getCoursesByOrganizer(int organizerId) {
        try {
            return courseDAO.findAll(); // TODO: implémenter recherche par organisateur
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Course> findCoursesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return courseDAO.findAll(); // TODO: implémenter recherche par période
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public int getAvailableSpots(int courseId) {
        try {
            Optional<Course> courseOpt = courseDAO.findById(courseId);
            if (!courseOpt.isPresent()) {
                return 0;
            }
            Course course = courseOpt.get();
            int currentParticipants = participationDAO.countByCourse(courseId);
            return Math.max(0, course.getMaxParticipants() - currentParticipants);
        } catch (SQLException e) {
            return 0;
        }
    }
    
    @Override
    public List<Course> getUpcomingCourses(int limit) {
        try {
            return courseDAO.findAll(); // TODO: implémenter recherche de courses à venir
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Course> getOpenCourses() {
        try {
            return courseDAO.findAll(); // TODO: implémenter recherche de courses ouvertes
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean deleteCourse(int courseId, boolean hardDelete) {
        try {
            if (hardDelete) {
                courseDAO.delete(courseId);
            } else {
                // TODO: implémenter soft delete
                courseDAO.delete(courseId);
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public boolean changeCourseStatus(int courseId, String status) {
        // TODO: implémenter changement de statut
        return false;
    }
    
    @Override
    public List<Course> findCoursesByLocation(String location) {
        try {
            return courseDAO.findAll(); // TODO: implémenter recherche par lieu
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public long getTotalCourseCount() {
        try {
            return courseDAO.findAll().size(); // TODO: optimiser avec une requête count
        } catch (SQLException e) {
            return 0;
        }
    }
    
    public List<Course> getFilteredCourses(String date, String city, String distance, String sort) throws SQLException {
        return courseDAO.findFiltered(date, city, distance, sort);
    }
    
    @Override
    public List<Course> findFiltered(String date, String city, String distance, String sort) throws SQLException {
        return getFilteredCourses(date, city, distance, sort);
    }
    
    @Override
    public List<Course> searchCourses(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Pour l'instant, recherche simple dans le nom et la description
        List<Course> allCourses = courseDAO.findAll();
        List<Course> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();
        
        for (Course course : allCourses) {
            if ((course.getName() != null && course.getName().toLowerCase().contains(lowerSearchTerm)) ||
                (course.getDescription() != null && course.getDescription().toLowerCase().contains(lowerSearchTerm)) ||
                (course.getCity() != null && course.getCity().toLowerCase().contains(lowerSearchTerm))) {
                results.add(course);
            }
        }
        
        return results;
    }
    
    @Override
    public List<String> getAllCities() throws SQLException {
        List<Course> allCourses = courseDAO.findAll();
        List<String> cities = new ArrayList<>();
        
        for (Course course : allCourses) {
            if (course.getCity() != null && !course.getCity().trim().isEmpty()) {
                String city = course.getCity().trim();
                if (!cities.contains(city)) {
                    cities.add(city);
                }
            }
        }
        
        return cities;
    }
    
    @Override
    public List<Double> getAllDistances() throws SQLException {
        List<Course> allCourses = courseDAO.findAll();
        List<Double> distances = new ArrayList<>();
        
        for (Course course : allCourses) {
            if (course.getDistance() > 0) {
                double distance = course.getDistance();
                if (!distances.contains(distance)) {
                    distances.add(distance);
                }
            }
        }
        
        return distances;
    }
    
    @Override
    public List<Course> findByCreator(int creatorId) throws SQLException {
        return courseDAO.findByCreator(creatorId);
    }
} 