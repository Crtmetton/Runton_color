package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.security.TokenManager;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet pour la page d'accueil
 * UTILISE JSP - Système original restauré
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/"})
public class HomeServlet extends HttpServlet {
    
    private CourseService courseService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.courseService = new CourseServiceImpl();
        Logger.info("HomeServlet", "✅ Service courses initialisé");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Logger.separator("ACCÈS PAGE D'ACCUEIL");
        Logger.step("HomeServlet", "🔄 Traitement de la requête d'accueil");
        
        try {
            // Configurer l'encodage UTF-8
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            Logger.debug("HomeServlet", "Encodage UTF-8 configuré");
            
            // Récupérer les messages de session
            Logger.step("HomeServlet", "🔄 Récupération des messages de session");
            Logger.stepSuccess("HomeServlet", "Messages de session traités");
            
            // Récupérer les prochaines courses (max 6 pour l'accueil)
            Logger.step("HomeServlet", "🔄 Récupération des prochaines courses");
            List<Course> upcomingCourses = courseService.getAllCourses();
            
            // Limiter à 6 courses pour l'accueil
            if (upcomingCourses != null && upcomingCourses.size() > 6) {
                upcomingCourses = upcomingCourses.subList(0, 6);
            }
            
            Logger.info("HomeServlet", 
                "Courses trouvées: " + (upcomingCourses != null ? upcomingCourses.size() : 0));
            Logger.stepSuccess("HomeServlet", "Courses récupérées");
            
            // Ajouter les courses à la requête pour le JSP
            request.setAttribute("upcomingCourses", upcomingCourses);
            
            // Afficher les messages flash s'ils existent
            String successMessage = (String) request.getSession().getAttribute("success");
            String errorMessage = (String) request.getSession().getAttribute("error");
            String infoMessage = (String) request.getSession().getAttribute("info");
            
            if (successMessage != null) {
                Logger.info("HomeServlet", "Message succès: " + successMessage);
            }
            if (errorMessage != null) {
                Logger.warn("HomeServlet", "Message erreur: " + errorMessage);
            }
            if (infoMessage != null) {
                Logger.info("HomeServlet", "Message info: " + infoMessage);
            }
            
            // FORWARD VERS JSP - L'ancien système qui marchait !
            Logger.info("HomeServlet", "✅ Utilisation du JSP d'origine comme demandé");
            request.getRequestDispatcher("/WEB-INF/views/acceuil-2.jsp").forward(request, response);
            Logger.stepSuccess("HomeServlet", "Page d'accueil affichée (JSP)");
            
        } catch (Exception e) {
            Logger.error("HomeServlet", "❌ Erreur lors du chargement de l'accueil", e);
            
            // Fallback simple en cas d'erreur
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<!DOCTYPE html>");
            response.getWriter().println("<html><head><title>Runton Color</title></head>");
            response.getWriter().println("<body><h1>Erreur temporaire</h1>");
            response.getWriter().println("<p>L'application se charge... Veuillez rafraîchir la page.</p>");
            response.getWriter().println("</body></html>");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Pour l'instant, rediriger vers GET
        doGet(request, response);
    }
} 