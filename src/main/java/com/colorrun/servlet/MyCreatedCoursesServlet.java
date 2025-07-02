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
 * Servlet pour afficher les courses créées par l'organisateur/admin connecté
 * Accessible uniquement aux organisateurs et administrateurs
 */
public class MyCreatedCoursesServlet extends HttpServlet {
    
    private CourseService courseService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.courseService = new CourseServiceImpl();
        Logger.success("MyCreatedCoursesServlet", "✅ Service initialisé");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Logger.step("MyCreatedCoursesServlet", "🔄 Accès page mes courses créées");
        
        // Vérifier l'authentification et les permissions avec TokenManager
        if (!TokenManager.isAuthenticated(request)) {
            Logger.warn("MyCreatedCoursesServlet", "Utilisateur non authentifié");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            if (!TokenManager.requireOrganizerRole(request, response)) {
                return; // requireOrganizerRole gère déjà la redirection/erreur
            }
        } catch (Exception e) {
            Logger.error("MyCreatedCoursesServlet", "Erreur lors de la vérification des permissions", e);
            response.sendRedirect(request.getContextPath() + "/?error=permission_error");
            return;
        }
        
        // Récupérer l'utilisateur connecté
        UserToken currentToken = TokenManager.getToken(request);
        if (currentToken == null || !currentToken.isAuthenticated()) {
            Logger.error("MyCreatedCoursesServlet", "Token non valide");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Logger.info("MyCreatedCoursesServlet", "Utilisateur connecté: " + currentToken.getFullName() + " (Role: " + currentToken.getRole() + ")");
        
        try {
            Logger.step("MyCreatedCoursesServlet", "🔄 Récupération des courses créées");
            
            // Récupérer les courses créées par cet utilisateur
            List<Course> createdCourses = courseService.findByCreator(currentToken.getUserId());
            
            Logger.info("MyCreatedCoursesServlet", "Courses créées trouvées: " + createdCourses.size());
            Logger.stepSuccess("MyCreatedCoursesServlet", "Courses récupérées");
            
            // Ajouter les données à la requête
            request.setAttribute("createdCourses", createdCourses);
            request.setAttribute("userToken", currentToken);
            request.setAttribute("isAuthenticated", true);
            
            // Ajouter les informations d'authentification à la requête pour la navbar
            TokenManager.addTokenToRequest(request);
            
            // Gestion des messages de session
            HttpSession session = request.getSession(false);
            if (session != null) {
                String success = (String) session.getAttribute("success");
                String error = (String) session.getAttribute("error");
                if (success != null) {
                    request.setAttribute("success", success);
                    session.removeAttribute("success");
                    Logger.success("MyCreatedCoursesServlet", "Message succès: " + success);
                }
                if (error != null) {
                    request.setAttribute("error", error);
                    session.removeAttribute("error");
                    Logger.error("MyCreatedCoursesServlet", "Message erreur: " + error);
                }
            }
            
            Logger.info("MyCreatedCoursesServlet", "✅ Affichage de la page mes courses créées");
            Logger.stepSuccess("MyCreatedCoursesServlet", "Forward vers my-created-courses.jsp");
            
            // Forward vers le template JSP
            request.getRequestDispatcher("/WEB-INF/views/my-created-courses.jsp").forward(request, response);
            
        } catch (Exception e) {
            Logger.error("MyCreatedCoursesServlet", "Erreur lors de la récupération des courses créées", e);
            request.setAttribute("error", "Erreur technique lors du chargement de vos courses créées");
            request.setAttribute("createdCourses", List.of()); // Liste vide
            request.setAttribute("userToken", currentToken);
            request.setAttribute("isAuthenticated", true);
            
            // Ajouter les informations d'authentification à la requête pour la navbar
            TokenManager.addTokenToRequest(request);
            
            // Afficher la page avec l'erreur
            request.getRequestDispatcher("/WEB-INF/views/my-created-courses.jsp").forward(request, response);
        }
    }
} 