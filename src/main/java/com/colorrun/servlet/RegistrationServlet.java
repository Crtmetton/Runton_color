package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.business.User;

public class RegistrationServlet extends HttpServlet {
    
    UserService userService;
    
    public RegistrationServlet() {
        this.userService = new UserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.html").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        // Validate input
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            req.setAttribute("error", "All fields are required");
            req.getRequestDispatcher("/WEB-INF/views/register.html").forward(req, resp);
            return;
        }
        
        try {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPasswordHash(password);
            user.setRole("PARTICIPANT");
            
            userService.register(user);
            
            // Generate base URL for verification link
            String baseUrl = req.getRequestURL().toString().replace("/register", "");
            
            // Send verification email
            userService.sendVerificationEmail(user, baseUrl);
            
            req.setAttribute("success", "Registration successful. Please check your email to verify your account.");
            req.getRequestDispatcher("/WEB-INF/views/register-success.html").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/register.html").forward(req, resp);
        }
    }
}
