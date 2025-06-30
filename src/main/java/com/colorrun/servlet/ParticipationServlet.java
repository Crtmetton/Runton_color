package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.impl.ParticipationServiceImpl;
import com.colorrun.service.DossardService;
import com.colorrun.service.impl.DossardServiceImpl;
import com.colorrun.service.EmailService;
import com.colorrun.service.impl.EmailServiceImpl;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Dossard;
import com.colorrun.business.Participation;

public class ParticipationServlet extends HttpServlet {
    
    private final ParticipationService participationService;
    private final DossardService dossardService;
    private final EmailService emailService;
    private final CourseService courseService;
    
    public ParticipationServlet() {
        this.participationService = new ParticipationServiceImpl();
        this.dossardService = new DossardServiceImpl();
        this.emailService = new EmailServiceImpl();
        this.courseService = new CourseServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to register for a course");
            return;
        }
        
        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // Inscrire l'utilisateur à la course
            int participationId = participationService.register(user.getId(), courseId);
            
            // Générer le dossard
            Dossard dossard = dossardService.genererDossard(participationId);
            
            // Générer le PDF du dossard
            byte[] pdfBytes = dossardService.genererPdfDossard(dossard);
            
            // Récupérer les informations de la course pour l'email
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (!courseOpt.isPresent()) {
                throw new Exception("Course non trouvée : " + courseId);
            }
            Course course = courseOpt.get();
            
            try {
                // Envoyer le dossard par email
                emailService.sendDossardEmail(user, course, dossard, pdfBytes);
                
                // Envoyer aussi l'email de confirmation d'inscription
                emailService.sendCourseRegistrationConfirmation(user, course);
                
                // Rediriger avec message de succès
                HttpSession session = req.getSession();
                session.setAttribute("success", 
                    "Inscription réussie ! Votre dossard a été envoyé par email à " + user.getEmail());
                resp.sendRedirect(req.getContextPath() + "/courses");
                
            } catch (Exception emailError) {
                // Si l'envoi d'email échoue, on propose le téléchargement direct
                System.err.println("Erreur envoi email dossard : " + emailError.getMessage());
                
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "attachment; filename=dossard_" + dossard.getNumber() + ".pdf");
                resp.getOutputStream().write(pdfBytes);
            }
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to cancel a registration");
            return;
        }
        
        String participationIdParam = req.getParameter("id");
        if (participationIdParam == null || participationIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Participation ID is required");
            return;
        }
        
        try {
            int participationId = Integer.parseInt(participationIdParam);
            participationService.cancel(participationId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid participation ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
