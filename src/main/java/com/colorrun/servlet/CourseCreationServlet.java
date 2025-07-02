package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
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
        request.getRequestDispatcher("/WEB-INF/views/creation-course.jsp").forward(request, response);
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
            // Récupération des paramètres correspondant aux champs de la table COURSE
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String lieu = request.getParameter("lieu");
            String dateStr = request.getParameter("date");
            String heureStr = request.getParameter("heure");
            String distanceStr = request.getParameter("distance");
            String maxParticipantsStr = request.getParameter("maxParticipants");
            String prixStr = request.getParameter("prix");
            String cause = request.getParameter("cause");
            
            Logger.info("CourseCreationServlet", "Paramètres reçus pour création course");
            Logger.debug("CourseCreationServlet", "nom: " + (nom != null ? "✓" : "✗"));
            Logger.debug("CourseCreationServlet", "lieu: " + (lieu != null ? lieu : "✗"));
            Logger.debug("CourseCreationServlet", "date: " + (dateStr != null ? dateStr : "✗"));
            Logger.debug("CourseCreationServlet", "heure: " + (heureStr != null ? heureStr : "✗"));
            
            // Validation des champs obligatoires
            if (nom == null || nom.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                lieu == null || lieu.trim().isEmpty() ||
                dateStr == null || dateStr.trim().isEmpty() ||
                heureStr == null || heureStr.trim().isEmpty()) {
                
                Logger.error("CourseCreationServlet", "Champs obligatoires manquants");
                request.setAttribute("error", "Tous les champs obligatoires doivent être remplis.");
                request.getRequestDispatcher("/WEB-INF/views/creation-course.jsp").forward(request, response);
                return;
            }
            
            // Conversion de la date et heure
            String dateTimeStr = dateStr + "T" + heureStr + ":00";
            LocalDateTime courseDate = LocalDateTime.parse(dateTimeStr);
            
            // Conversion des valeurs numériques avec validation
            double distance = Double.parseDouble(distanceStr != null && !distanceStr.trim().isEmpty() ? distanceStr : "5.0");
            int maxParticipants = Integer.parseInt(maxParticipantsStr != null && !maxParticipantsStr.trim().isEmpty() ? maxParticipantsStr : "100");
            int prix = Integer.parseInt(prixStr != null && !prixStr.trim().isEmpty() ? prixStr : "0");
            
            Logger.step("CourseCreationServlet", "Création de l'objet Course");
            
            // Récupérer l'utilisateur connecté pour définir le créateur
            UserToken currentToken = TokenManager.getToken(request);
            if (currentToken == null || !currentToken.isAuthenticated()) {
                Logger.error("CourseCreationServlet", "Impossible de récupérer l'utilisateur connecté");
                request.setAttribute("error", "Erreur d'authentification. Veuillez vous reconnecter.");
                request.getRequestDispatcher("/WEB-INF/views/creation-course.jsp").forward(request, response);
                return;
            }
            
            // Créer la course avec tous les champs de la table
            Course course = new Course();
            course.setName(nom.trim());
            course.setDescription(description.trim());
            course.setCity(lieu.trim()); // lieu correspond au champ LIEU de la table
            course.setDate(courseDate);
            course.setDistance(distance);
            course.setMaxParticipants(maxParticipants);
            course.setPrix(prix);
            if (cause != null && !cause.trim().isEmpty()) {
                course.setCause(cause.trim());
            }
            course.setUserCreateId(currentToken.getUserId()); // Définir qui a créé cette course
            
            Logger.info("CourseCreationServlet", "Course créée: " + nom + " à " + lieu + " par " + currentToken.getFullName());
            Logger.stepSuccess("CourseCreationServlet", "Objet Course créé");
            
            Logger.step("CourseCreationServlet", "Enregistrement en base de données");
            
            // Sauvegarder en base
            courseService.createCourse(course);
            
            Logger.success("CourseCreationServlet", "Course créée avec succès!");
            Logger.stepSuccess("CourseCreationServlet", "Processus création");
            
            // Redirection avec message de succès
            session.setAttribute("success", "Course '" + nom + "' créée avec succès!");
            response.sendRedirect(request.getContextPath() + "/courses");
            
        } catch (Exception e) {
            Logger.error("CourseCreationServlet", "Erreur lors de la création", e);
            request.setAttribute("error", "Erreur lors de la création de la course. Veuillez réessayer.");
            request.getRequestDispatcher("/WEB-INF/views/creation-course.jsp").forward(request, response);
        }
    }
} 