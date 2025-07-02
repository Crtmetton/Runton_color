package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import com.colorrun.service.CourseService;
import com.colorrun.service.DiscussionService;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.service.impl.DiscussionServiceImpl;
import com.colorrun.service.impl.ParticipationServiceImpl;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Discussion;
import com.colorrun.business.Participation;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;
import java.time.format.DateTimeFormatter;

public class CourseDetailServlet extends HttpServlet {
    
    private final CourseService courseService;
    private final DiscussionService discussionService;
    private final ParticipationService participationService;
    
    public CourseDetailServlet() {
        this.courseService = new CourseServiceImpl();
        this.discussionService = new DiscussionServiceImpl();
        this.participationService = new ParticipationServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            Optional<Course> courseOpt = courseService.findById(id);
            
            if (!courseOpt.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                return;
            }
            
            Course course = courseOpt.get();
            
            // Récupérer l'utilisateur connecté via TokenManager
            UserToken currentToken = TokenManager.getToken(req);
            boolean isAuthenticated = currentToken != null && currentToken.isAuthenticated();
            
            // Vérifier si l'utilisateur connecté est le créateur de la course
            boolean isCreator = false;
            if (isAuthenticated && course.getUserCreateId() > 0) {
                isCreator = currentToken.getUserId() == course.getUserCreateId();
            }
            
            // Récupérer les messages de discussion de la course (triés par date ASC - plus anciens en haut)
            List<Discussion> discussions = discussionService.findByCourse(id);
            Logger.debug("CourseDetailServlet", "Récupération de " + discussions.size() + " messages pour la course " + id);
            
            // Si c'est une requête AJAX pour actualiser les messages
            String ajaxParam = req.getParameter("ajax");
            if ("1".equals(ajaxParam)) {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                
                StringBuilder json = new StringBuilder("{\"messages\": [");
                for (int i = 0; i < discussions.size(); i++) {
                    Discussion discussion = discussions.get(i);
                    if (i > 0) json.append(",");
                    json.append("{");
                    json.append("\"id\": ").append(discussion.getId()).append(",");
                    json.append("\"content\": \"").append(escapeJson(discussion.getContenu())).append("\",");
                    json.append("\"timestamp\": \"").append(discussion.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\",");
                    json.append("\"sender\": {");
                    if (discussion.getExpediteur() != null) {
                        json.append("\"firstName\": \"").append(escapeJson(discussion.getExpediteur().getFirstName())).append("\",");
                        json.append("\"lastName\": \"").append(escapeJson(discussion.getExpediteur().getLastName())).append("\"");
                    } else {
                        json.append("\"firstName\": \"Utilisateur\",");
                        json.append("\"lastName\": \"Inconnu\"");
                    }
                    json.append("}");
                    json.append("}");
                }
                json.append("]}");
                
                resp.getWriter().write(json.toString());
                return;
            }
            
            // Récupérer les participants
            List<Participation> participants = participationService.findByCourse(id);
            
            // Vérifier si l'utilisateur est déjà inscrit
            boolean isRegistered = false;
            if (isAuthenticated) {
                isRegistered = participationService.isUserRegistered(currentToken.getUserId(), id);
            }
            
            // Ajouter les informations d'authentification à la requête pour la navbar
            TokenManager.addTokenToRequest(req);
            
            // Passer les données au template
            req.setAttribute("course", course);
            req.setAttribute("discussions", discussions); // Changé de "messages" à "discussions"
            req.setAttribute("participants", participants);
            req.setAttribute("isRegistered", isRegistered);
            req.setAttribute("isAuthenticated", isAuthenticated);
            req.setAttribute("isCreator", isCreator);
            // Afficher la popup de participation si l'utilisateur est authentifié et pas encore inscrit
            req.setAttribute("showParticipationPopup", isAuthenticated && !isRegistered);
            
            Logger.info("CourseDetailServlet", "Affichage course " + course.getName() + " avec " + discussions.size() + " messages");
            
            // Afficher le template
            req.getRequestDispatcher("/WEB-INF/views/info-course.jsp").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            Logger.error("CourseDetailServlet", "Erreur lors de l'affichage de la course: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        // Récupérer l'utilisateur connecté via TokenManager
        UserToken currentToken = TokenManager.getToken(req);
        if (currentToken == null || !currentToken.isAuthenticated()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to post messages");
            return;
        }
        
        String courseIdParam = req.getParameter("courseId");
        String content = req.getParameter("content");
        
        Logger.debug("CourseDetailServlet", "Tentative d'envoi de message - courseId: " + courseIdParam + ", content: " + (content != null ? content.length() + " chars" : "null"));
        
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            Logger.error("CourseDetailServlet", "Course ID manquant");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        if (content == null || content.trim().isEmpty()) {
            Logger.error("CourseDetailServlet", "Contenu du message manquant");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message content is required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // Vérifier que la course existe
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (!courseOpt.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                return;
            }
            
            // Créer le message de discussion
            discussionService.create(courseId, currentToken.getUserId(), content.trim());
            
            Logger.info("CourseDetailServlet", "Message créé avec succès pour la course " + courseId + " par l'utilisateur " + currentToken.getUserId());
            
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            Logger.error("CourseDetailServlet", "Course ID invalide: " + courseIdParam);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            Logger.error("CourseDetailServlet", "Erreur lors de la création du message: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    /**
     * Échappe les caractères spéciaux pour JSON
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
