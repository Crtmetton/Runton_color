package com.colorrun.servlet;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.security.TokenManager;
import com.colorrun.security.UserToken;
import com.colorrun.service.MessageService;
import com.colorrun.service.UserService;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.MessageServiceImpl;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.util.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet de gestion des messages et discussions.
 * 
 * Gère l'envoi, la réception et l'affichage des messages entre utilisateurs.
 * Supporte les messages privés et les discussions publiques de courses.
 */
public class MessageServlet extends HttpServlet {
    
    private final MessageService messageService;
    private final UserService userService;
    private final CourseService courseService;
    
    public MessageServlet() {
        this.messageService = new MessageServiceImpl();
        this.userService = new UserServiceImpl();
        this.courseService = new CourseServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        
        // Vérification de la connexion
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");
        
        try {
            if (action == null) {
                action = "inbox";
            }
            
            switch (action) {
                case "inbox":
                    showInbox(req, resp, user);
                    break;
                case "sent":
                    showSentMessages(req, resp, user);
                    break;
                case "compose":
                    showComposeForm(req, resp);
                    break;
                case "conversation":
                    showConversation(req, resp, user);
                    break;
                case "course-discussion":
                    showCourseDiscussion(req, resp, user);
                    break;
                case "messages":
                    handleGetMessages(req, resp);
                    break;
                default:
                    showInbox(req, resp, user);
            }
            
        } catch (SQLException e) {
            req.setAttribute("error", "Erreur lors du chargement des messages : " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/messages/error.html").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        
        // Vérification de la connexion
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = req.getParameter("action");
        
        try {
            switch (action) {
                case "send":
                    sendMessage(req, resp, user);
                    break;
                case "send-course":
                    sendCourseMessage(req, resp, user);
                    break;
                case "mark-read":
                    markAsRead(req, resp, user);
                    break;
                case "delete":
                    handleDeleteMessage(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/messages");
            }
            
        } catch (SQLException e) {
            req.setAttribute("error", "Erreur lors de l'envoi du message : " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/messages/error.html").forward(req, resp);
        }
    }
    
    /**
     * Affiche la boîte de réception
     */
    private void showInbox(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, ServletException, IOException {
        
        List<Message> receivedMessages = messageService.getReceivedMessages(user.getId());
        int unreadCount = messageService.getUnreadMessageCount(user.getId());
        
        req.setAttribute("messages", receivedMessages);
        req.setAttribute("unreadCount", unreadCount);
        req.setAttribute("currentPage", "inbox");
        
        req.getRequestDispatcher("/WEB-INF/views/messages/inbox.html").forward(req, resp);
    }
    
    /**
     * Affiche les messages envoyés
     */
    private void showSentMessages(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, ServletException, IOException {
        
        List<Message> sentMessages = messageService.getSentMessages(user.getId());
        
        req.setAttribute("messages", sentMessages);
        req.setAttribute("currentPage", "sent");
        
        req.getRequestDispatcher("/WEB-INF/views/messages/sent.html").forward(req, resp);
    }
    
    /**
     * Affiche le formulaire de composition
     */
    private void showComposeForm(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String recipientId = req.getParameter("to");
        if (recipientId != null && !recipientId.isEmpty()) {
            try {
                User recipient = userService.findById(Integer.parseInt(recipientId)).orElse(null);
                req.setAttribute("recipient", recipient);
            } catch (Exception e) {
                // Ignore l'erreur
            }
        }
        
        req.getRequestDispatcher("/WEB-INF/views/messages/compose.html").forward(req, resp);
    }
    
    /**
     * Affiche une conversation privée
     */
    private void showConversation(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, ServletException, IOException {
        
        String otherUserIdParam = req.getParameter("with");
        if (otherUserIdParam == null || otherUserIdParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/messages");
            return;
        }
        
        try {
            int otherUserId = Integer.parseInt(otherUserIdParam);
            User otherUser = userService.findById(otherUserId).orElse(null);
            
            if (otherUser == null) {
                req.setAttribute("error", "Utilisateur non trouvé");
                req.getRequestDispatcher("/WEB-INF/views/messages/error.html").forward(req, resp);
                return;
            }
            
            List<Message> conversation = messageService.getPrivateConversation(user.getId(), otherUserId);
            
            req.setAttribute("messages", conversation);
            req.setAttribute("otherUser", otherUser);
            req.setAttribute("currentPage", "conversation");
            
            req.getRequestDispatcher("/WEB-INF/views/messages/conversation.html").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/messages");
        }
    }
    
    /**
     * Affiche la discussion d'une course
     */
    private void showCourseDiscussion(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, ServletException, IOException {
        
        String courseIdParam = req.getParameter("course");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/courses");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            List<Message> courseMessages = messageService.getCourseMessages(courseId);
            
            req.setAttribute("messages", courseMessages);
            req.setAttribute("courseId", courseId);
            req.setAttribute("currentPage", "course-discussion");
            
            req.getRequestDispatcher("/WEB-INF/views/messages/course-discussion.html").forward(req, resp);
            
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }
    
    /**
     * Envoie un message privé
     */
    private void sendMessage(HttpServletRequest req, HttpServletResponse resp, User sender) 
            throws SQLException, ServletException, IOException {
        
        String recipientIdParam = req.getParameter("recipientId");
        String content = req.getParameter("content");
        
        if (recipientIdParam == null || content == null || content.trim().isEmpty()) {
            req.setAttribute("error", "Veuillez remplir tous les champs");
            showComposeForm(req, resp);
            return;
        }
        
        try {
            int recipientId = Integer.parseInt(recipientIdParam);
            
            // Vérifier que le destinataire existe
            User recipient = userService.findById(recipientId).orElse(null);
            if (recipient == null) {
                req.setAttribute("error", "Destinataire non trouvé");
                showComposeForm(req, resp);
                return;
            }
            
            // Envoyer le message
            messageService.sendPrivateMessage(sender.getId(), recipientId, content.trim());
            
            req.setAttribute("success", "Message envoyé avec succès");
            resp.sendRedirect(req.getContextPath() + "/messages?action=sent");
            
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Identifiant destinataire invalide");
            showComposeForm(req, resp);
        }
    }
    
    /**
     * Envoie un message dans une discussion de course
     */
    private void sendCourseMessage(HttpServletRequest req, HttpServletResponse resp, User sender) 
            throws SQLException, ServletException, IOException {
        
        String courseIdParam = req.getParameter("courseId");
        String content = req.getParameter("content");
        
        if (courseIdParam == null || content == null || content.trim().isEmpty()) {
            req.setAttribute("error", "Veuillez remplir tous les champs");
            showCourseDiscussion(req, resp, sender);
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // Envoyer le message public
            messageService.sendPublicMessage(sender.getId(), courseId, content.trim());
            
            resp.sendRedirect(req.getContextPath() + "/messages?action=course-discussion&course=" + courseId);
            
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Identifiant course invalide");
            showCourseDiscussion(req, resp, sender);
        }
    }
    
    /**
     * Marque un message comme lu
     */
    private void markAsRead(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, IOException {
        
        String messageIdParam = req.getParameter("messageId");
        
        if (messageIdParam != null) {
            try {
                int messageId = Integer.parseInt(messageIdParam);
                messageService.markAsRead(messageId, user.getId());
            } catch (NumberFormatException e) {
                // Ignore l'erreur
            }
        }
        
        resp.sendRedirect(req.getContextPath() + "/messages");
    }
    
    /**
     * Gère la suppression d'un message par l'organisateur de la course
     */
    private void handleDeleteMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Vérification de l'authentification
            UserToken currentToken = TokenManager.getToken(req);
            if (currentToken == null) {
                Logger.error("MessageServlet", "Tentative de suppression sans authentification");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"Non authentifié\"}");
                return;
            }
            
            String messageIdStr = req.getParameter("messageId");
            if (messageIdStr == null || messageIdStr.trim().isEmpty()) {
                Logger.error("MessageServlet", "ID de message manquant");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID de message manquant\"}");
                return;
            }
            
            int messageId = Integer.parseInt(messageIdStr);
            Logger.info("MessageServlet", "Tentative de suppression du message " + messageId + " par " + currentToken.getEmail());
            
            // Récupérer le message
            Message message = messageService.getMessageById(messageId);
            if (message == null) {
                Logger.error("MessageServlet", "Message " + messageId + " non trouvé");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Message non trouvé\"}");
                return;
            }
            
            // Vérifier que l'utilisateur a les droits de supprimer ce message
            if (!canUserDeleteMessage(currentToken, message)) {
                Logger.error("MessageServlet", "Utilisateur " + currentToken.getEmail() + " non autorisé à supprimer le message " + messageId);
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\": \"Non autorisé à supprimer ce message\"}");
                return;
            }
            
            // Supprimer le message
            boolean deleted = messageService.deleteMessage(messageId, currentToken.getUserId());
            
            if (deleted) {
                Logger.success("MessageServlet", "Message " + messageId + " supprimé avec succès");
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\": true, \"message\": \"Message supprimé\"}");
            } else {
                Logger.error("MessageServlet", "Échec de la suppression du message " + messageId);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Erreur lors de la suppression\"}");
            }
            
        } catch (NumberFormatException e) {
            Logger.error("MessageServlet", "ID de message invalide", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID de message invalide\"}");
        } catch (Exception e) {
            Logger.error("MessageServlet", "Erreur lors de la suppression du message", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Erreur interne du serveur\"}");
        }
    }
    
    /**
     * Récupère les messages d'une course pour l'actualisation AJAX
     */
    private void handleGetMessages(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String courseIdStr = req.getParameter("courseId");
            if (courseIdStr == null || courseIdStr.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID de course manquant\"}");
                return;
            }
            
            int courseId = Integer.parseInt(courseIdStr);
            List<Message> messages = messageService.getCourseMessages(courseId);
            
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            
            out.print("{\"messages\": [");
            for (int i = 0; i < messages.size(); i++) {
                Message msg = messages.get(i);
                if (i > 0) out.print(",");
                out.print("{");
                out.print("\"id\": " + msg.getId() + ",");
                out.print("\"content\": \"" + escapeJson(msg.getContent()) + "\",");
                out.print("\"timestamp\": \"" + msg.getTimestamp() + "\",");
                out.print("\"sender\": {");
                out.print("\"firstName\": \"" + escapeJson(msg.getSender().getFirstName()) + "\",");
                out.print("\"lastName\": \"" + escapeJson(msg.getSender().getLastName()) + "\"");
                out.print("}");
                out.print("}");
            }
            out.print("]}");
            
        } catch (NumberFormatException e) {
            Logger.error("MessageServlet", "ID de course invalide", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID de course invalide\"}");
        } catch (Exception e) {
            Logger.error("MessageServlet", "Erreur lors de la récupération des messages", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Erreur interne du serveur\"}");
        }
    }
    
    /**
     * Vérifie si l'utilisateur peut supprimer un message
     */
    private boolean canUserDeleteMessage(UserToken token, Message message) {
        try {
            // L'utilisateur peut supprimer ses propres messages
            if (message.getSender().getId() == token.getUserId()) {
                return true;
            }
            
            // Les administrateurs peuvent supprimer tous les messages
            if ("ADMIN".equals(token.getRole())) {
                return true;
            }
            
            // L'organisateur de la course peut supprimer les messages de sa course
            if (message.getCourseId() != null) {
                Course course = courseService.findById(message.getCourseId()).orElse(null);
                if (course != null && course.getUserCreateId() == token.getUserId()) {
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            Logger.error("MessageServlet", "Erreur lors de la vérification des droits", e);
            return false;
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