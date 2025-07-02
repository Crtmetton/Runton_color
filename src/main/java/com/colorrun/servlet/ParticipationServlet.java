package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.impl.ParticipationServiceImpl;
import com.colorrun.service.DossardService;
import com.colorrun.service.impl.DossardServiceImpl;
import com.colorrun.service.EmailService;
import com.colorrun.service.impl.EmailServiceImpl;
import com.colorrun.service.CourseService;
import com.colorrun.service.MessageService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.service.impl.MessageServiceImpl;
import com.colorrun.service.QRCodeService;
import com.colorrun.service.impl.QRCodeServiceImpl;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Dossard;
import com.colorrun.business.Participation;
import com.colorrun.business.Message;

public class ParticipationServlet extends HttpServlet {
    
    private final ParticipationService participationService;
    private final DossardService dossardService;
    private final EmailService emailService;
    private final CourseService courseService;
    private final MessageService messageService;
    private final QRCodeService qrCodeService;
    
    public ParticipationServlet() {
        this.participationService = new ParticipationServiceImpl();
        this.dossardService = new DossardServiceImpl();
        this.emailService = new EmailServiceImpl();
        this.courseService = new CourseServiceImpl();
        this.messageService = new MessageServiceImpl();
        this.qrCodeService = new QRCodeServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            Optional<Course> courseOpt = courseService.findById(courseId);
            
            if (!courseOpt.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                return;
            }
            
            Course course = courseOpt.get();
            User user = (User) req.getSession().getAttribute("user");
            
            // Si l'utilisateur n'est pas connecté, rediriger vers la page de détails
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/courseDetail?id=" + courseId);
                return;
            }
            
            // Récupérer les messages de la course
            List<Message> messages = messageService.findByCourse(courseId);
            
            // Récupérer les participants
            List<Participation> participants = participationService.findByCourse(courseId);
            
            // Vérifier si l'utilisateur est déjà inscrit
            boolean isRegistered = participationService.isUserRegistered(user.getId(), courseId);
            
            // Passer les données au template avec le popup de participation
            req.setAttribute("course", course);
            req.setAttribute("messages", messages);
            req.setAttribute("participants", participants);
            req.setAttribute("isRegistered", isRegistered);
            req.setAttribute("isAuthenticated", true);
            req.setAttribute("showParticipationPopup", true);
            
            // Afficher le template
            req.getRequestDispatcher("/WEB-INF/views/info-course.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Utiliser le système de tokens au lieu de la session
        UserToken token = TokenManager.getToken(req);
        if (token == null || !token.isAuthenticated()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to register for a course");
            return;
        }
        
        // Créer un objet User à partir du token
        User user = new User();
        user.setId(token.getUserId());
        user.setFirstName(token.getFirstName());
        user.setLastName(token.getLastName());
        user.setEmail(token.getEmail());
        user.setRole(token.getRole());
        
        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // Inscrire l'utilisateur à la course
            int participationId = participationService.register(user.getId(), courseId);
            
            // Récupérer les informations de la course pour l'email et le QR code
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (!courseOpt.isPresent()) {
                throw new Exception("Course non trouvée : " + courseId);
            }
            Course course = courseOpt.get();
            
            // Créer un objet Participation pour le QR code
            Participation participation = new Participation();
            participation.setId(participationId);
            participation.setUser(user);
            participation.setCourse(course);
            
            try {
                // Générer le QR code
                byte[] qrCodeBytes = qrCodeService.generateQRCode(participation);
                
                // Envoyer l'email avec QR code
                sendParticipationConfirmationWithQR(user, course, participation, qrCodeBytes);
                
                // Rediriger avec message de succès
                HttpSession session = req.getSession();
                session.setAttribute("success", 
                    "Inscription réussie ! Votre QR code de participation a été envoyé par email à " + user.getEmail());
                resp.sendRedirect(req.getContextPath() + "/courses");
                
            } catch (Exception emailError) {
                // Si l'envoi d'email échoue, on affiche un message et redirige vers "mes courses"
                System.err.println("Erreur envoi email QR code : " + emailError.getMessage());
                
                HttpSession session = req.getSession();
                session.setAttribute("info", 
                    "Inscription réussie ! Vous pouvez récupérer votre QR code dans la section 'Mes Courses'.");
                resp.sendRedirect(req.getContextPath() + "/courses");
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  TENTATIVE DE DÉSINSCRIPTION");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            System.out.println("❌ Utilisateur non connecté");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to cancel a registration");
            return;
        }
        
        System.out.println("✅ Utilisateur connecté: " + user.getFirstName() + " " + user.getLastName() + " (ID: " + user.getId() + ")");
        
        // Accepter soit participationId (ancien système) soit courseId (nouveau système)
        String participationIdParam = req.getParameter("id");
        String courseIdParam = req.getParameter("courseId");
        
        System.out.println("📋 Paramètres reçus:");
        System.out.println("   - participationId: " + participationIdParam);
        System.out.println("   - courseId: " + courseIdParam);
        
        try {
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                // Nouveau système - désinscription par courseId
                int courseId = Integer.parseInt(courseIdParam);
                System.out.println("🔄 Désinscription par courseId: " + courseId);
                
                // Vérifier que l'utilisateur est bien inscrit à cette course
                boolean isRegistered = participationService.isUserRegistered(user.getId(), courseId);
                System.out.println("🔍 Vérification inscription: " + (isRegistered ? "✅ Inscrit" : "❌ Non inscrit"));
                
                if (!isRegistered) {
                    System.out.println("❌ Erreur: Utilisateur non inscrit à cette course");
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "You are not registered for this course");
                    return;
                }
                
                // Désinscription par userId et courseId
                System.out.println("🔄 Tentative de désinscription...");
                participationService.cancelParticipation(user.getId(), courseId);
                System.out.println("✅ Désinscription réussie !");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                
            } else if (participationIdParam != null && !participationIdParam.isEmpty()) {
                // Ancien système - désinscription par participationId
                int participationId = Integer.parseInt(participationIdParam);
                System.out.println("🔄 Désinscription par participationId: " + participationId);
                participationService.cancel(participationId);
                System.out.println("✅ Désinscription réussie !");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                
            } else {
                System.out.println("❌ Erreur: Aucun ID fourni");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Either participation ID or course ID is required");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Erreur de format ID: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            System.out.println("❌ Erreur interne: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    /**
     * Envoie l'email de confirmation d'inscription avec QR code.
     */
    private void sendParticipationConfirmationWithQR(User user, Course course, Participation participation, byte[] qrCodeBytes) throws Exception {
        String subject = "Confirmation d'inscription - " + course.getName() + " (QR Code inclus)";
        String content = buildParticipationEmailContent(user, course, participation);
        
        emailService.sendCustomEmail(user.getEmail(), subject, content);
    }
    
    /**
     * Construit le contenu HTML de l'email de confirmation avec QR code.
     */
    private String buildParticipationEmailContent(User user, Course course, Participation participation) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>Inscription confirmée ! 🎉</h1>" +
               "<p>Bonjour " + user.getFirstName() + ",</p>" +
               "<p>Votre inscription à <strong>" + course.getName() + "</strong> a été confirmée !</p>" +
               "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
               "<h3 style='color: #e74c3c; margin-top: 0;'>Détails de la course</h3>" +
               "<p><strong>📍 Lieu :</strong> " + course.getCity() + "</p>" +
               "<p><strong>📅 Date :</strong> " + course.getDate() + "</p>" +
               "<p><strong>🏃‍♂️ Distance :</strong> " + course.getDistance() + " km</p>" +
               "<p><strong>🎯 ID Participation :</strong> " + participation.getId() + "</p>" +
               "</div>" +
               "<div style='background-color: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #28a745;'>" +
               "<h3 style='color: #28a745; margin-top: 0;'>📱 Votre QR Code de participation</h3>" +
               "<p>Votre QR Code personnel est maintenant disponible dans votre espace 'Mes Courses'.</p>" +
               "<p><strong>Instructions :</strong></p>" +
               "<ul>" +
               "<li>Connectez-vous à votre compte</li>" +
               "<li>Allez dans la section 'Mes Courses'</li>" +
               "<li>Cliquez sur 'Dossard' pour recevoir votre QR Code par email</li>" +
               "<li>Présentez ce QR Code le jour de la course</li>" +
               "</ul>" +
               "</div>" +
               "<h3>Le jour de la course :</h3>" +
               "<ul>" +
               "<li>📱 Ayez votre QR Code sur votre téléphone ou imprimé</li>" +
               "<li>⏰ Arrivez 30 minutes avant le départ</li>" +
               "<li>👕 N'oubliez pas vos vêtements blancs !</li>" +
               "<li>💧 Pensez à apporter de l'eau</li>" +
               "</ul>" +
               "<div style='text-align: center; margin: 30px 0;'>" +
               "<a href='http://localhost:8080/runton-color/my-courses' style='background-color: #e74c3c; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Accéder à mes courses</a>" +
               "</div>" +
               "<p>Nous avons hâte de vous voir participer à cette Color Run !</p>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
}
