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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Servlet pour la modification des courses existantes
 * Accessible seulement par le créateur de la course
 */
public class CourseEditServlet extends HttpServlet {
    
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
        
        Logger.step("CourseEditServlet", "Accès page modification de course");
        
        // Vérifier l'authentification
        if (!TokenManager.isAuthenticated(request)) {
            Logger.warn("CourseEditServlet", "Utilisateur non authentifié");
            response.sendRedirect(request.getContextPath() + "/?error=auth_required");
            return;
        }
        
        // Récupérer l'ID de la course
        String courseIdParam = request.getParameter("id");
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            Logger.error("CourseEditServlet", "ID de course manquant");
            response.sendRedirect(request.getContextPath() + "/courses?error=course_id_required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            Optional<Course> courseOpt = courseService.findById(courseId);
            
            if (!courseOpt.isPresent()) {
                Logger.error("CourseEditServlet", "Course non trouvée avec ID: " + courseId);
                response.sendRedirect(request.getContextPath() + "/courses?error=course_not_found");
                return;
            }
            
            Course course = courseOpt.get();
            UserToken currentToken = TokenManager.getToken(request);
            
            // Vérifier que l'utilisateur connecté est le créateur de la course
            if (course.getUserCreateId() != currentToken.getUserId()) {
                Logger.error("CourseEditServlet", "Accès refusé - utilisateur " + currentToken.getEmail() + 
                           " n'est pas le créateur de la course " + courseId);
                response.sendRedirect(request.getContextPath() + "/course/detail?id=" + courseId + "&error=access_denied");
                return;
            }
            
            Logger.success("CourseEditServlet", "Accès autorisé pour modification de la course " + course.getName());
            
            // Ajouter les informations d'authentification à la requête pour la navbar
            TokenManager.addTokenToRequest(request);
            
            // Passer la course au formulaire
            request.setAttribute("course", course);
            request.setAttribute("isEdit", true);
            
            Logger.stepSuccess("CourseEditServlet", "Affichage formulaire modification");
            
            // Afficher le formulaire de modification (réutilise le formulaire de création)
            request.getRequestDispatcher("/WEB-INF/views/edit-course.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            Logger.error("CourseEditServlet", "ID de course invalide: " + courseIdParam);
            response.sendRedirect(request.getContextPath() + "/courses?error=invalid_course_id");
        } catch (Exception e) {
            Logger.error("CourseEditServlet", "Erreur lors de l'accès à la page de modification", e);
            response.sendRedirect(request.getContextPath() + "/courses?error=internal_error");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Logger.step("CourseEditServlet", "Traitement modification de course");
        
        // Vérifier l'authentification
        if (!TokenManager.isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/?error=auth_required");
            return;
        }
        
        HttpSession session = request.getSession();
        String courseIdParam = request.getParameter("courseId");
        
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            Logger.error("CourseEditServlet", "ID de course manquant lors de la modification");
            response.sendRedirect(request.getContextPath() + "/courses?error=course_id_required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            Optional<Course> courseOpt = courseService.findById(courseId);
            
            if (!courseOpt.isPresent()) {
                Logger.error("CourseEditServlet", "Course non trouvée lors de la modification: " + courseId);
                response.sendRedirect(request.getContextPath() + "/courses?error=course_not_found");
                return;
            }
            
            Course existingCourse = courseOpt.get();
            UserToken currentToken = TokenManager.getToken(request);
            
            // Vérifier que l'utilisateur connecté est le créateur de la course
            if (existingCourse.getUserCreateId() != currentToken.getUserId()) {
                Logger.error("CourseEditServlet", "Tentative de modification non autorisée par " + currentToken.getEmail());
                response.sendRedirect(request.getContextPath() + "/course/detail?id=" + courseId + "&error=access_denied");
                return;
            }
            
            // Récupération des paramètres du formulaire
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            String lieu = request.getParameter("lieu");
            String dateStr = request.getParameter("date");
            String heureStr = request.getParameter("heure");
            String distanceStr = request.getParameter("distance");
            String maxParticipantsStr = request.getParameter("maxParticipants");
            String prixStr = request.getParameter("prix");
            String cause = request.getParameter("cause");
            
            Logger.info("CourseEditServlet", "Paramètres reçus pour modification course " + courseId);
            
            // Validation des champs obligatoires
            if (nom == null || nom.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                lieu == null || lieu.trim().isEmpty() ||
                dateStr == null || dateStr.trim().isEmpty() ||
                heureStr == null || heureStr.trim().isEmpty()) {
                
                Logger.error("CourseEditServlet", "Champs obligatoires manquants");
                request.setAttribute("error", "Tous les champs obligatoires doivent être remplis.");
                request.setAttribute("course", existingCourse);
                request.setAttribute("isEdit", true);
                request.getRequestDispatcher("/WEB-INF/views/edit-course.jsp").forward(request, response);
                return;
            }
            
            // Conversion de la date et heure
            String dateTimeStr = dateStr + "T" + heureStr + ":00";
            LocalDateTime courseDate = LocalDateTime.parse(dateTimeStr);
            
            // Conversion des valeurs numériques avec validation
            double distance = Double.parseDouble(distanceStr != null && !distanceStr.trim().isEmpty() ? distanceStr : "5.0");
            int maxParticipants = Integer.parseInt(maxParticipantsStr != null && !maxParticipantsStr.trim().isEmpty() ? maxParticipantsStr : "100");
            int prix = Integer.parseInt(prixStr != null && !prixStr.trim().isEmpty() ? prixStr : "0");
            
            Logger.step("CourseEditServlet", "Mise à jour de l'objet Course");
            
            // Mettre à jour la course existante
            existingCourse.setName(nom.trim());
            existingCourse.setDescription(description.trim());
            existingCourse.setCity(lieu.trim());
            existingCourse.setDate(courseDate);
            existingCourse.setDistance(distance);
            existingCourse.setMaxParticipants(maxParticipants);
            existingCourse.setPrix(prix);
            if (cause != null && !cause.trim().isEmpty()) {
                existingCourse.setCause(cause.trim());
            }
            // Le userCreateId reste inchangé
            
            Logger.info("CourseEditServlet", "Course modifiée: " + nom + " à " + lieu + " par " + currentToken.getFullName());
            Logger.stepSuccess("CourseEditServlet", "Objet Course mis à jour");
            
            Logger.step("CourseEditServlet", "Sauvegarde en base de données");
            
            // Sauvegarder les modifications
            courseService.update(existingCourse);
            
            Logger.success("CourseEditServlet", "Course modifiée avec succès!");
            Logger.stepSuccess("CourseEditServlet", "Processus modification");
            
            // Redirection avec message de succès
            session.setAttribute("success", "Course '" + nom + "' modifiée avec succès!");
            response.sendRedirect(request.getContextPath() + "/course/detail?id=" + courseId);
            
        } catch (NumberFormatException e) {
            Logger.error("CourseEditServlet", "Erreur de format des données numériques", e);
            request.setAttribute("error", "Erreur dans les données numériques. Vérifiez la distance, le nombre de participants et le prix.");
            response.sendRedirect(request.getContextPath() + "/course/edit?id=" + courseIdParam + "&error=format_error");
        } catch (Exception e) {
            Logger.error("CourseEditServlet", "Erreur lors de la modification", e);
            request.setAttribute("error", "Erreur lors de la modification de la course. Veuillez réessayer.");
            response.sendRedirect(request.getContextPath() + "/course/edit?id=" + courseIdParam + "&error=internal_error");
        }
    }
} 