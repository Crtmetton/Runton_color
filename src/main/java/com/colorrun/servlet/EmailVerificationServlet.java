package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import com.colorrun.service.VerificationTokenService;
import com.colorrun.service.impl.VerificationTokenServiceImpl;
import com.colorrun.service.EmailService;
import com.colorrun.service.impl.EmailServiceImpl;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.business.User;

/**
 * Servlet de vérification des adresses email.
 * 
 * Cette servlet traite les liens de vérification envoyés par email
 * lors de l'inscription d'un nouvel utilisateur.
 */
public class EmailVerificationServlet extends HttpServlet {
    
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final UserService userService;
    
    public EmailVerificationServlet() {
        this.verificationTokenService = new VerificationTokenServiceImpl();
        this.emailService = new EmailServiceImpl();
        this.userService = new UserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        
        if (token == null || token.isEmpty()) {
            req.setAttribute("error", "Token de vérification manquant.");
            req.getRequestDispatcher("/WEB-INF/views/verification-result.jsp").forward(req, resp);
            return;
        }
        
        try {
            boolean verified = verificationTokenService.validateToken(token);
            
            if (verified) {
                req.setAttribute("success", "Votre compte a été activé avec succès ! Vous pouvez maintenant vous connecter.");
                
                // Optionnel : Envoyer un email de bienvenue
                // (nécessiterait de récupérer l'utilisateur depuis le token)
                
            } else {
                req.setAttribute("error", "Le lien de vérification est invalide ou a expiré. Veuillez demander un nouveau lien.");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification : " + e.getMessage());
            req.setAttribute("error", "Une erreur s'est produite lors de la vérification. Veuillez réessayer.");
        }
        
        req.getRequestDispatcher("/WEB-INF/views/verification-result.jsp").forward(req, resp);
    }
}
