package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import com.colorrun.service.OrganizerRequestService;
import com.colorrun.service.impl.OrganizerRequestServiceImpl;
import com.colorrun.business.User;

public class OrganizerRequestServlet extends HttpServlet {
    
    OrganizerRequestService organizerRequestService;
    
    public OrganizerRequestServlet() {
        this.organizerRequestService = new OrganizerRequestServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            boolean hasRequest = organizerRequestService.hasRequestForUser(user.getId());
            req.setAttribute("hasRequest", hasRequest);
            req.getRequestDispatcher("/WEB-INF/views/organizer-request.html").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to submit a request");
            return;
        }
        
        String motivation = req.getParameter("motivation");
        if (motivation == null || motivation.trim().isEmpty()) {
            req.setAttribute("error", "Motivation is required");
            req.getRequestDispatcher("/WEB-INF/views/organizer-request.html").forward(req, resp);
            return;
        }
        
        try {
            organizerRequestService.create(user.getId(), motivation);
            req.setAttribute("success", "Your request has been submitted successfully");
            req.setAttribute("hasRequest", true);
            req.getRequestDispatcher("/WEB-INF/views/organizer-request.html").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/organizer-request.html").forward(req, resp);
        }
    }
}
