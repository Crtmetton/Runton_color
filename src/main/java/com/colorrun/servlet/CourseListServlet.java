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
 * Servlet pour afficher la liste des courses
 * UTILISE JSP - Comme demandé par l'utilisateur
 */
@WebServlet(name = "CourseListServlet", urlPatterns = {"/courses"})
public class CourseListServlet extends HttpServlet {
    
    private CourseService courseService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.courseService = new CourseServiceImpl();
        Logger.info("CourseListServlet", "✅ Service courses initialisé");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Logger.separator("ACCÈS LISTE COURSES");
        Logger.step("CourseListServlet", "🔄 Chargement liste courses");
        
        try {
            // Configurer l'encodage
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            
            // Les informations du token sont déjà ajoutées par AuthenticationFilter
            String userName = (String) request.getAttribute("userName");
            String userRole = (String) request.getAttribute("userRole");
            Boolean isAuthenticated = (Boolean) request.getAttribute("isAuthenticated");
            
            Logger.debug("CourseListServlet", 
                "Utilisateur: " + (isAuthenticated ? userName + " (" + userRole + ")" : "Non connecté"));
            
            // Récupérer toutes les courses
            Logger.step("CourseListServlet", "🔄 Récupération courses depuis BDD");
            List<Course> courses = courseService.getAllCourses();
            
            if (courses != null) {
                Logger.success("CourseListServlet", 
                    "✅ " + courses.size() + " course(s) récupérée(s)");
                
                // Log détaillé des courses
                for (Course course : courses) {
                    Logger.debug("CourseListServlet", 
                        "Course: " + course.getName() + " - " + course.getCity() + 
                        " (" + course.getDistance() + "km)");
                }
            } else {
                Logger.warn("CourseListServlet", "⚠️ Aucune course trouvée");
                courses = List.of(); // Liste vide pour éviter les erreurs
            }
            
            // Ajouter les courses à la requête pour le JSP
            request.setAttribute("courses", courses);
            
            // Messages flash (si présents) - les garder en session pour le JSP
            String success = (String) request.getSession().getAttribute("success");
            String error = (String) request.getSession().getAttribute("error");
            String info = (String) request.getSession().getAttribute("info");
            
            if (success != null) {
                Logger.info("CourseListServlet", "Message succès: " + success);
            }
            if (error != null) {
                Logger.warn("CourseListServlet", "Message erreur: " + error);
            }
            if (info != null) {
                Logger.info("CourseListServlet", "Message info: " + info);
            }
            
            // FORWARD VERS JSP - L'ancien système !
            Logger.info("CourseListServlet", "✅ Utilisation du JSP d'origine comme demandé");
            request.getRequestDispatcher("/WEB-INF/views/courses/simple-list.jsp").forward(request, response);
            Logger.stepSuccess("CourseListServlet", "Page courses affichée (JSP)");
            
        } catch (Exception e) {
            Logger.error("CourseListServlet", "❌ Erreur lors du chargement des courses", e);
            
            // En cas d'erreur, rediriger vers l'accueil avec message
            request.getSession().setAttribute("error", 
                "Erreur technique lors du chargement des courses");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Pour l'instant, rediriger vers GET
        doGet(request, response);
    }
}
