package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import com.colorrun.security.TokenManager;
import com.colorrun.util.Logger;

public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Définir l'encodage pour les caractères spéciaux
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        
        // Récupérer la page de redirection (si spécifiée)
        String redirectTo = req.getParameter("redirect");
        
        Logger.separator("DÉCONNEXION UTILISATEUR");
        
        // Invalider la session et détruire le token
        HttpSession session = req.getSession(false);
        if (session != null) {
            Logger.info("LogoutServlet", "Déconnexion de l'utilisateur");
            
            // Détruire le token utilisateur
            TokenManager.destroyToken(session);
            
            session.invalidate();
            Logger.success("LogoutServlet", "Session et token invalidés avec succès");
        }
        
        // Déterminer où rediriger l'utilisateur
        String redirectUrl;
        if (redirectTo != null && !redirectTo.isEmpty()) {
            // Valider que l'URL de redirection est sûre (même domaine)
            if (redirectTo.startsWith("/") && !redirectTo.startsWith("//")) {
                redirectUrl = req.getContextPath() + redirectTo;
            } else {
                redirectUrl = req.getContextPath() + "/";
            }
        } else {
            // Par défaut, retourner à l'accueilz
            redirectUrl = req.getContextPath() + "/";
        }
        
        resp.sendRedirect(redirectUrl);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Rediriger les POST vers GET
        doGet(req, resp);
    }
} 