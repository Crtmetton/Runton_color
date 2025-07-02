package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import com.colorrun.service.DiscussionService;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.DiscussionServiceImpl;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.business.Discussion;
import com.colorrun.business.Course;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.util.Logger;

/**
 * Servlet REST (HTTP DELETE uniquement) pour la suppression d'un message de
 * discussion d'une course.
 * <p>
 * Elle vérifie :
 * <ul>
 *   <li>Authentification de l'utilisateur via {@link com.colorrun.security.TokenManager}</li>
 *   <li>Existence du message et de la course</li>
 *   <li>Permissions : auteur du message, organisateur de la course ou admin</li>
 * </ul>
 * En cas de succès, la réponse est <code>204 No&nbsp;Content</code>.
 * </p>
 */
public class DiscussionServlet extends HttpServlet {
    
    private final DiscussionService discussionService;
    private final CourseService courseService;
    
    public DiscussionServlet() {
        this.discussionService = new DiscussionServiceImpl();
        this.courseService = new CourseServiceImpl();
    }
    
    /**
     * Supprime un message de discussion.
     * <p>Répond avec les codes HTTP appropriés : 400, 401, 403, 404 ou 204.</p>
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        // Récupérer l'utilisateur connecté via TokenManager
        UserToken currentToken = TokenManager.getToken(req);
        if (currentToken == null || !currentToken.isAuthenticated()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to delete messages");
            return;
        }
        
        String idParam = req.getParameter("id");
        
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message ID is required");
            return;
        }
        
        try {
            int messageId = Integer.parseInt(idParam);
            
            // Récupérer le message
            Optional<Discussion> discussionOpt = discussionService.findById(messageId);
            if (!discussionOpt.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Message not found");
                return;
            }
            
            Discussion discussion = discussionOpt.get();
            
            // Récupérer la course pour vérifier les permissions
            Optional<Course> courseOpt = courseService.findById(discussion.getCourseId());
            if (!courseOpt.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                return;
            }
            
            Course course = courseOpt.get();
            
            // Vérifier les permissions de suppression
            boolean canDelete = false;
            
            // L'organisateur de la course peut supprimer tous les messages
            if (course.getUserCreateId() == currentToken.getUserId()) {
                canDelete = true;
                Logger.info("DiscussionServlet", "Suppression autorisée pour l'organisateur de la course " + course.getId());
            }
            // L'administrateur peut supprimer tous les messages
            else if ("ADMIN".equals(currentToken.getRole())) {
                canDelete = true;
                Logger.info("DiscussionServlet", "Suppression autorisée pour l'administrateur");
            }
            // L'auteur du message peut supprimer son propre message
            else if (discussion.getExpediteurId() == currentToken.getUserId()) {
                canDelete = true;
                Logger.info("DiscussionServlet", "Suppression autorisée pour l'auteur du message");
            }
            
            if (!canDelete) {
                Logger.warn("DiscussionServlet", "Tentative de suppression non autorisée par l'utilisateur " + currentToken.getUserId());
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to delete this message");
                return;
            }
            
            // Supprimer le message
            discussionService.delete(messageId);
            
            Logger.info("DiscussionServlet", "Message " + messageId + " supprimé avec succès par l'utilisateur " + currentToken.getUserId());
            
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            Logger.error("DiscussionServlet", "Message ID invalide: " + idParam);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message ID");
        } catch (Exception e) {
            Logger.error("DiscussionServlet", "Erreur lors de la suppression du message: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
} 