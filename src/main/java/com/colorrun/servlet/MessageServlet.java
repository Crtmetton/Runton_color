package com.colorrun.servlet;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import com.colorrun.service.MessageService;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.MessageServiceImpl;
import com.colorrun.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    
    public MessageServlet() {
        this.messageService = new MessageServiceImpl();
        this.userService = new UserServiceImpl();
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
                    deleteMessage(req, resp, user);
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
     * Supprime un message
     */
    private void deleteMessage(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, IOException {
        
        String messageIdParam = req.getParameter("messageId");
        
        if (messageIdParam != null) {
            try {
                int messageId = Integer.parseInt(messageIdParam);
                messageService.deleteMessage(messageId, user.getId());
            } catch (NumberFormatException e) {
                // Ignore l'erreur
            }
        }
        
        resp.sendRedirect(req.getContextPath() + "/messages");
    }
} 