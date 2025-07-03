package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.colorrun.service.OrganizerRequestService;
import com.colorrun.service.impl.OrganizerRequestServiceImpl;
import com.colorrun.business.User;

public class AdminServlet extends HttpServlet {

    private final OrganizerRequestService organizerRequestService;

    public AdminServlet() {
        this.organizerRequestService = new OrganizerRequestServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            req.setAttribute("requests", organizerRequestService.findPending());
            req.getRequestDispatcher("/WEB-INF/views/admin/requests.html").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be an admin to perform this action");
            return;
        }

        int requestId = Integer.parseInt(req.getParameter("requestId"));
        String action = req.getParameter("action");

        try {
            if ("approve".equals(action)) {
                organizerRequestService.approve(requestId);
            } else if ("reject".equals(action)) {
                organizerRequestService.reject(requestId);
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}