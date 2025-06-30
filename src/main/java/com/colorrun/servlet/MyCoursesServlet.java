package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.business.User;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet pour afficher les courses de l'utilisateur
 * UTILISE LE FICHIER HTML mes-courses.html COMME DEMANDÉ
 * Mapping configuré dans web.xml sur /profile
 */
public class MyCoursesServlet extends HttpServlet {
    
    private CourseService courseService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.courseService = new CourseServiceImpl();
        Logger.success("MyCoursesServlet", "✅ Service courses initialisé");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Logger.step("MyCoursesServlet", "🔄 Accès page mes courses");
        
        // Utiliser le système de tokens au lieu des sessions
        UserToken userToken = (UserToken) request.getAttribute("userToken");
        if (userToken == null) {
            Logger.warn("MyCoursesServlet", "Utilisateur non authentifié");
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        
        Logger.info("MyCoursesServlet", "Utilisateur connecté: " + userToken.getFullName());
        
        try {
            Logger.step("MyCoursesServlet", "🔄 Récupération des courses utilisateur");
            
            // Pour l'instant, récupérer toutes les courses
            // TODO: Implémenter la logique selon le rôle (organisées vs participées)
            List<Course> userCourses = courseService.getAllCourses();
            
            Logger.info("MyCoursesServlet", "Courses trouvées: " + userCourses.size());
            Logger.stepSuccess("MyCoursesServlet", "Courses récupérées");
            
            // Ajouter les données à la requête
            request.setAttribute("courses", userCourses);
            request.setAttribute("user", userToken);
            
            // Gestion des messages de session
            HttpSession session = request.getSession(false);
            if (session != null) {
                String success = (String) session.getAttribute("success");
                String error = (String) session.getAttribute("error");
                if (success != null) {
                    request.setAttribute("success", success);
                    session.removeAttribute("success");
                    Logger.success("MyCoursesServlet", "Message succès: " + success);
                }
                if (error != null) {
                    request.setAttribute("error", error);
                    session.removeAttribute("error");
                    Logger.error("MyCoursesServlet", "Message erreur: " + error);
                }
            }
            
            Logger.info("MyCoursesServlet", "✅ Redirection vers le fichier HTML mes-courses.html comme demandé");
            Logger.stepSuccess("MyCoursesServlet", "Redirection vers mes-courses.html effectuée");
            
            // Rediriger vers la page HTML (PAS JSP!)
            response.sendRedirect(request.getContextPath() + "/mes-courses.html");
            
        } catch (Exception e) {
            Logger.error("MyCoursesServlet", "Erreur lors de la récupération des courses", e);
            // En cas d'erreur, rediriger vers la page avec un paramètre d'erreur
            response.sendRedirect(request.getContextPath() + "/mes-courses.html?error=1");
        }
    }
} 