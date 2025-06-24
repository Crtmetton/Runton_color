package com.colorrun.service.impl;

import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import com.colorrun.business.User;
import com.colorrun.dao.CourseDAO;
import com.colorrun.dao.ParticipationDAO;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.ParticipationService;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class ParticipationServiceImpl implements ParticipationService {
    
    private ParticipationDAO participationDAO;
    private UserDAO userDAO;
    private CourseDAO courseDAO;
    
    public ParticipationServiceImpl() {
        this.participationDAO = new ParticipationDAO();
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();
    }
    
    @Override
    public void registerParticipation(int userId, int courseId) throws SQLException {
        // Vérifier si l'utilisateur existe
        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            throw new SQLException("User not found");
        }
        
        // Vérifier si la course existe
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (!courseOpt.isPresent()) {
            throw new SQLException("Course not found");
        }
        
        Course course = courseOpt.get();
        
        // Vérifier si la course est complète
        int currentParticipants = participationDAO.countByCourse(courseId);
        if (currentParticipants >= course.getMaxParticipants()) {
            throw new SQLException("Course is already full");
        }
        
        // Vérifier si l'utilisateur est déjà inscrit
        Optional<Participation> existingParticipation = participationDAO.findByUserAndCourse(userId, courseId);
        if (existingParticipation.isPresent()) {
            throw new SQLException("User is already registered for this course");
        }
        
        // Créer une nouvelle participation
        Participation participation = new Participation();
        participation.setUser(userOpt.get());
        participation.setCourse(course);
        participation.setDate(LocalDateTime.now());
        participation.setStatus("REGISTERED");
        
        // Enregistrer la participation
        participationDAO.save(participation);
    }
    
    @Override
    public List<Participation> getUserParticipations(int userId) throws SQLException {
        return participationDAO.findByUser(userId);
    }
    
    @Override
    public List<Participation> getCourseParticipations(int courseId) throws SQLException {
        return participationDAO.findByCourse(courseId);
    }
    
    @Override
    public boolean isUserRegistered(int userId, int courseId) throws SQLException {
        return participationDAO.findByUserAndCourse(userId, courseId).isPresent();
    }
    
    @Override
    public void cancelParticipation(int userId, int courseId) throws SQLException {
        Optional<Participation> participationOpt = participationDAO.findByUserAndCourse(userId, courseId);
        
        if (!participationOpt.isPresent()) {
            throw new SQLException("Participation not found");
        }
        
        Participation participation = participationOpt.get();
        participation.setStatus("CANCELED");
        
        participationDAO.update(participation);
    }
    
    @Override
    public List<Course> getUserCourses(int userId) throws SQLException {
        List<Participation> participations = participationDAO.findByUser(userId);
        return participations.stream()
                .map(Participation::getCourse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    // Méthodes de compatibilité avec les servlets existants
    
    @Override
    public int register(int userId, int courseId) throws SQLException {
        registerParticipation(userId, courseId);
        Optional<Participation> participation = participationDAO.findByUserAndCourse(userId, courseId);
        return participation.isPresent() ? participation.get().getId() : 0;
    }
    
    @Override
    public Optional<Participation> findById(int id) throws SQLException {
        return participationDAO.findById(id);
    }
    
    @Override
    public List<Participation> findByUser(int userId) throws SQLException {
        return getUserParticipations(userId);
    }
    
    @Override
    public List<Participation> findByCourse(int courseId) throws SQLException {
        return getCourseParticipations(courseId);
    }
    
    @Override
    public List<Course> findCoursesByUser(int userId) throws SQLException {
        return getUserCourses(userId);
    }
    
    @Override
    public void cancel(int participationId) throws SQLException {
        Optional<Participation> participationOpt = participationDAO.findById(participationId);
        if (!participationOpt.isPresent()) {
            throw new SQLException("Participation not found");
        }
        
        Participation participation = participationOpt.get();
        cancelParticipation(participation.getUser().getId(), participation.getCourse().getId());
    }
    
    @Override
    public byte[] generateBibPdf(int participationId) throws SQLException {
        // Implémentation simplifiée pour générer un PDF de dossard
        Optional<Participation> participationOpt = participationDAO.findById(participationId);
        if (!participationOpt.isPresent()) {
            throw new SQLException("Participation not found");
        }
        
        try {
            // Simuler la génération d'un PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write("BIB PDF".getBytes());
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SQLException("Failed to generate bib PDF", e);
        }
    }
} 