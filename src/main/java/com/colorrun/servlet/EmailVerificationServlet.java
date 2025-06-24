package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;

public class EmailVerificationServlet extends HttpServlet {
    
    private final UserService userService;
    
    public EmailVerificationServlet() {
        this.userService = new UserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        
        if (token == null || token.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is required");
            return;
        }
        
        try {
            boolean verified = userService.verifyEmail(token);
            
            if (verified) {
                req.setAttribute("message", "Votre compte a été activé avec succès. Vous pouvez maintenant vous connecter.");
            } else {
                req.setAttribute("error", "Le lien de vérification est invalide ou a expiré.");
            }
            
            req.getRequestDispatcher("/WEB-INF/views/verification-result.html").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
