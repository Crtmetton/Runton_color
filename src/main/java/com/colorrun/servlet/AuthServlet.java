package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.business.User;

public class AuthServlet extends HttpServlet {

    private final UserService userService;

    public AuthServlet() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/acceuil-2.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            userService.login(email, password).ifPresentOrElse(user -> {
                try {
                    HttpSession session = req.getSession();
                    session.setAttribute("user", user);

                    switch (user.getRole()) {
                        case "ADMIN":
                            resp.sendRedirect(req.getContextPath() + "/admin/requests");
                            break;
                        case "ORGANIZER":
                            resp.sendRedirect(req.getContextPath() + "/organizer/courses");
                            break;
                        default:
                            resp.sendRedirect(req.getContextPath() + "/courses");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                try {
                    req.setAttribute("error", "Invalid email or password");
                    req.getRequestDispatcher("/WEB-INF/views/login.html").forward(req, resp);
                } catch (ServletException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.html").forward(req, resp);
        }
    }
}
