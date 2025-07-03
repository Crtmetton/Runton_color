package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.impl.ParticipationServiceImpl;
import com.colorrun.business.User;

public class ParticipationServlet extends HttpServlet {
    
    ParticipationService participationService;
    
    public ParticipationServlet() {
        this.participationService = new ParticipationServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to register for a course");
            return;
        }
        
        String courseIdParam = req.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            int bibNumber = participationService.register(user.getId(), courseId);
            
            // Generate and return PDF
            byte[] pdfBytes = participationService.generateBibPdf(bibNumber);
            
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=dossard_" + bibNumber + ".pdf");
            resp.getOutputStream().write(pdfBytes);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to cancel a registration");
            return;
        }
        
        String participationIdParam = req.getParameter("id");
        if (participationIdParam == null || participationIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Participation ID is required");
            return;
        }
        
        try {
            int participationId = Integer.parseInt(participationIdParam);
            participationService.cancel(participationId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid participation ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
