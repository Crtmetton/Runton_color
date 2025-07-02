package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.business.User;
import com.colorrun.service.CourseService;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.service.impl.ParticipationServiceImpl;
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
 * Affiche les courses auxquelles l'utilisateur participe
 * Mapping configuré dans web.xml sur /MyCourses
 */
public class MyCoursesServlet extends HttpServlet {
    
    private CourseService courseService;
    private ParticipationService participationService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.courseService = new CourseServiceImpl();
        this.participationService = new ParticipationServiceImpl();
        Logger.success("MyCoursesServlet", "✅ Services initialisés");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Logger.step("MyCoursesServlet", "🔄 Accès page mes courses");
        
        // Récupérer l'utilisateur depuis la session
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            Logger.warn("MyCoursesServlet", "Utilisateur non authentifié");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Logger.info("MyCoursesServlet", "Utilisateur connecté: " + user.getFirstName() + " " + user.getLastName());
        
        try {
            Logger.step("MyCoursesServlet", "🔄 Récupération des courses utilisateur");
            
            // Récupérer les courses auxquelles l'utilisateur participe
            List<Course> userCourses = participationService.findCoursesByUser(user.getId());
            
            Logger.info("MyCoursesServlet", "Courses trouvées: " + userCourses.size());
            Logger.stepSuccess("MyCoursesServlet", "Courses récupérées");
            
            // Ajouter les données à la requête
            request.setAttribute("userCourses", userCourses);
            request.setAttribute("user", user);
            request.setAttribute("isAuthenticated", true);
            
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
            
            Logger.info("MyCoursesServlet", "✅ Affichage de la page mes-courses avec template JSP");
            Logger.stepSuccess("MyCoursesServlet", "Forward vers mes-courses.jsp");
            
            // Forward vers le template JSP dynamique
            request.getRequestDispatcher("/WEB-INF/views/mes-courses.jsp").forward(request, response);
            
        } catch (Exception e) {
            Logger.error("MyCoursesServlet", "Erreur lors de la récupération des courses", e);
            request.setAttribute("error", "Erreur technique lors du chargement de vos courses");
            request.setAttribute("userCourses", List.of()); // Liste vide
            request.setAttribute("user", user);
            request.setAttribute("isAuthenticated", true);
            
            // Afficher la page avec l'erreur
            request.getRequestDispatcher("/WEB-INF/views/mes-courses.jsp").forward(request, response);
        }
    }
} 