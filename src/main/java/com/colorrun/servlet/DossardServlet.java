package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import com.colorrun.business.Dossard;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.CourseService;
import com.colorrun.service.QRCodeService;
import com.colorrun.service.PDFDossardService;
import com.colorrun.service.impl.ParticipationServiceImpl;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.service.impl.QRCodeServiceImpl;
import com.colorrun.service.impl.PDFDossardServiceImpl;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet pour la gestion des dossards avec QR codes.
 * 
 * Ce servlet gère :
 * - L'envoi de dossards par email avec QR codes
 * - Le téléchargement direct de dossards
 * - La génération de QR codes pour les participations
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class DossardServlet extends HttpServlet {
    
    private final ParticipationService participationService;
    private final CourseService courseService;
    private final QRCodeService qrCodeService;
    private final PDFDossardService pdfDossardService;
    public DossardServlet() {
        this.participationService = new ParticipationServiceImpl();
        this.courseService = new CourseServiceImpl();
        this.qrCodeService = new QRCodeServiceImpl();
        this.pdfDossardService = new PDFDossardServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Logger.step("DossardServlet", "🔄 Demande de dossard reçue");
        
        // Vérifier l'authentification
        UserToken token = TokenManager.getToken(request);
        if (token == null || !token.isAuthenticated()) {
            Logger.warn("DossardServlet", "❌ Utilisateur non authentifié");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise");
            return;
        }
        
        String courseIdParam = request.getParameter("courseId");
        String action = request.getParameter("action");
        
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            Logger.error("DossardServlet", "❌ Course ID manquant");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID requis");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            int userId = token.getUserId();
            
            Logger.debug("DossardServlet", "Course: " + courseId + ", User: " + userId + ", Action: " + action);
            
            // Vérifier que l'utilisateur est inscrit à cette course
            if (!participationService.isUserRegistered(userId, courseId)) {
                Logger.warn("DossardServlet", "❌ Utilisateur non inscrit à cette course");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas inscrit à cette course");
                return;
            }
            
            // Récupérer les informations nécessaires
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (!courseOpt.isPresent()) {
                Logger.error("DossardServlet", "❌ Course non trouvée: " + courseId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
                return;
            }
            
            Course course = courseOpt.get();
            
            // Créer un objet User à partir du token
            User user = new User();
            user.setId(token.getUserId());
            user.setFirstName(token.getFirstName());
            user.setLastName(token.getLastName());
            user.setEmail(token.getEmail());
            user.setRole(token.getRole());
            
            // Créer un objet Participation pour le QR code
            Participation participation = new Participation();
            participation.setUser(user);
            participation.setCourse(course);
            // Note: L'ID de participation réel devrait être récupéré de la base
            participation.setId(generateParticipationId(userId, courseId));
            
            // Toujours générer et télécharger le PDF (plus besoin d'action)
            handlePDFDownload(response, participation, user, course);
            
        } catch (NumberFormatException e) {
            Logger.error("DossardServlet", "❌ Course ID invalide: " + courseIdParam);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID invalide");
        } catch (Exception e) {
            Logger.error("DossardServlet", "❌ Erreur interne: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la génération du dossard");
        }
    }
    
    /**
     * Gère le téléchargement du PDF de dossard avec QR code intégré.
     */
    private void handlePDFDownload(HttpServletResponse response, Participation participation, 
                                  User user, Course course) throws Exception {
        
        Logger.step("DossardServlet", "📄 Génération PDF dossard avec QR code");
        
        try {
            // Générer le QR code
            byte[] qrCodeBytes = qrCodeService.generateQRCode(participation);
            
            // Générer le PDF avec QR code intégré
            byte[] pdfBytes = pdfDossardService.generateDossardPDF(user, course, participation, qrCodeBytes);
            
            // Générer le nom de fichier
            String fileName = pdfDossardService.generateDossardFileName(course.getName(), participation.getId());
            
            // Configurer la réponse pour le téléchargement
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(pdfBytes.length);
            
            // Écrire le PDF dans la réponse
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            
            Logger.success("DossardServlet", "✅ PDF dossard téléchargé: " + fileName);
            
        } catch (Exception e) {
            Logger.error("DossardServlet", "❌ Erreur génération PDF: " + e.getMessage());
            e.printStackTrace();
            
            // En cas d'erreur, fallback vers QR code simple
            handleSimpleQRDownload(response, participation);
        }
    }
    
    /**
     * Fallback : téléchargement simple du QR code en cas d'erreur PDF.
     */
    private void handleSimpleQRDownload(HttpServletResponse response, Participation participation) throws Exception {
        
        Logger.step("DossardServlet", "💾 Fallback: Téléchargement QR code simple");
        
        byte[] qrCodeBytes = qrCodeService.generateQRCode(participation);
        
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", 
            "attachment; filename=qr_" + participation.getCourse().getName().replaceAll("\\s+", "_") + ".png");
        response.setContentLength(qrCodeBytes.length);
        
        response.getOutputStream().write(qrCodeBytes);
        response.getOutputStream().flush();
        
        Logger.success("DossardServlet", "✅ QR code simple téléchargé");
    }
    

    
    /**
     * Génère un ID de participation temporaire basé sur userId et courseId.
     * En production, ceci devrait récupérer le vrai ID depuis la base de données.
     */
    private int generateParticipationId(int userId, int courseId) {
        return (userId * 1000) + courseId;
    }
} 