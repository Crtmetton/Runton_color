package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.business.User;
import com.colorrun.security.TokenManager;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servlet pour la création de nouvelles courses
 */
public class CourseCreationServlet extends HttpServlet {
    
    private CourseService courseService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.courseService = new CourseServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Logger.step("CourseCreationServlet", "Accès page création de course");
        
        // Vérifier l'authentification et les permissions avec TokenManager
        if (!TokenManager.isAuthenticated(request)) {
            Logger.warn("CourseCreationServlet", "Utilisateur non authentifié");
            response.sendRedirect(request.getContextPath() + "/?error=auth_required");
            return;
        }
        
        try {
            if (!TokenManager.requireOrganizerRole(request, response)) {
                return; // requireOrganizerRole gère déjà la redirection/erreur
            }
        } catch (Exception e) {
            Logger.error("CourseCreationServlet", "Erreur lors de la vérification des permissions", e);
            response.sendRedirect(request.getContextPath() + "/?error=permission_error");
            return;
        }
        
        Logger.success("CourseCreationServlet", "Accès autorisé pour organisateur/admin");
        Logger.stepSuccess("CourseCreationServlet", "Affichage formulaire création");
        
        // Afficher le formulaire de création
        request.getRequestDispatcher("/WEB-INF/views/creation-course.html").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Logger.step("CourseCreationServlet", "Traitement création de course");
        
        // Vérifier l'authentification et les permissions avec TokenManager
        if (!TokenManager.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/?error=auth_required");
            return;
        }
        
        try {
            if (!TokenManager.requireOrganizerRole(request, response)) {
                return; // requireOrganizerRole gère déjà la redirection/erreur
            }
        } catch (Exception e) {
            Logger.error("CourseCreationServlet", "Erreur lors de la vérification des permissions", e);
            response.sendRedirect(request.getContextPath() + "/?error=permission_error");
            return;
        }
        
        HttpSession session = request.getSession();
        
        try {
            // Récupération des paramètres
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String city = request.getParameter("city");
            String dateStr = request.getParameter("date");
            String distanceStr = request.getParameter("distance");
            String maxParticipantsStr = request.getParameter("maxParticipants");
            
            Logger.info("CourseCreationServlet", "Paramètres reçus pour création course");
            Logger.debug("CourseCreationServlet", "name: " + (name != null ? "✓" : "✗"));
            Logger.debug("CourseCreationServlet", "city: " + (city != null ? city : "✗"));
            
            // Validation
            if (name == null || name.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                city == null || city.trim().isEmpty() ||
                dateStr == null || dateStr.trim().isEmpty()) {
                
                Logger.error("CourseCreationServlet", "Champs obligatoires manquants");
                request.setAttribute("error", "Tous les champs obligatoires doivent être remplis.");
                request.getRequestDispatcher("/WEB-INF/views/creation-course.html").forward(request, response);
                return;
            }
            
            // Conversion de la date
            LocalDateTime courseDate = LocalDateTime.parse(dateStr + "T10:00:00");
            
            // Conversion des valeurs numériques
            double distance = Double.parseDouble(distanceStr != null ? distanceStr : "5.0");
            int maxParticipants = Integer.parseInt(maxParticipantsStr != null ? maxParticipantsStr : "100");
            
            Logger.step("CourseCreationServlet", "Création de l'objet Course");
            
            // Créer la course
            Course course = new Course();
            course.setName(name.trim());
            course.setDescription(description.trim());
            course.setCity(city.trim());
            course.setDate(courseDate);
            course.setDistance(distance);
            course.setMaxParticipants(maxParticipants);
            
            Logger.info("CourseCreationServlet", "Course créée: " + name + " à " + city);
            Logger.stepSuccess("CourseCreationServlet", "Objet Course créé");
            
            Logger.step("CourseCreationServlet", "Enregistrement en base de données");
            
            // Sauvegarder en base
            courseService.createCourse(course);
            
            Logger.success("CourseCreationServlet", "Course créée avec succès!");
            Logger.stepSuccess("CourseCreationServlet", "Processus création");
            
            // Redirection avec message de succès
            session.setAttribute("success", "Course '" + name + "' créée avec succès!");
            response.sendRedirect(request.getContextPath() + "/courses");
            
        } catch (Exception e) {
            Logger.error("CourseCreationServlet", "Erreur lors de la création", e);
            request.setAttribute("error", "Erreur lors de la création de la course. Veuillez réessayer.");
            request.getRequestDispatcher("/WEB-INF/views/creation-course.html").forward(request, response);
        }
    }
} 