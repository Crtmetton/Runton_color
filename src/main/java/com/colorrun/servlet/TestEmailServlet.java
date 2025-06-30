package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import com.colorrun.service.EmailService;
import com.colorrun.service.impl.EmailServiceImpl;
import com.colorrun.config.EmailConfig;
import com.colorrun.business.User;

/**
 * Servlet de test pour diagnostiquer les problèmes d'emails.
 * Accessible via /test-email pour vérifier la configuration SMTP.
 */
public class TestEmailServlet extends HttpServlet {
    
    private final EmailService emailService;
    private final EmailConfig emailConfig;
    
    public TestEmailServlet() {
        this.emailService = new EmailServiceImpl();
        this.emailConfig = EmailConfig.getInstance();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Test Email - Color Run</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".config { background: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        out.println(".success { color: green; background: #d4edda; padding: 10px; border-radius: 5px; }");
        out.println(".error { color: red; background: #f8d7da; padding: 10px; border-radius: 5px; }");
        out.println(".form { margin: 20px 0; }");
        out.println("input[type='email'] { width: 300px; padding: 8px; }");
        out.println("input[type='submit'] { padding: 8px 20px; background: #007bff; color: white; border: none; border-radius: 4px; }");
        out.println("</style></head><body>");
        
        out.println("<h1>🧪 Test de Configuration Email</h1>");
        
        // Affichage de la configuration
        out.println("<h2>📋 Configuration actuelle</h2>");
        out.println("<div class='config'>");
        out.println("<strong>Service activé :</strong> " + emailConfig.isEnabled() + "<br>");
        out.println("<strong>Authentification configurée :</strong> " + emailConfig.isAuthenticationConfigured() + "<br>");
        out.println("<strong>Serveur SMTP :</strong> " + emailConfig.getSmtpHost() + ":" + emailConfig.getSmtpPort() + "<br>");
        out.println("<strong>Utilisateur :</strong> " + (emailConfig.getUsername() != null ? emailConfig.getUsername() : "NON CONFIGURÉ") + "<br>");
        out.println("<strong>Email expéditeur :</strong> " + emailConfig.getFromEmail() + "<br>");
        out.println("<strong>Nom expéditeur :</strong> " + emailConfig.getFromName() + "<br>");
        out.println("</div>");
        
        // Diagnostic
        out.println("<h2>🔍 Diagnostic</h2>");
        out.println("<div class='config'>");
        
        if (!emailConfig.isEnabled()) {
            out.println("<div class='error'>❌ Le service email est DÉSACTIVÉ</div>");
            out.println("<p>Pour l'activer, ajoutez : <code>-Dcolorrun.email.enabled=true</code></p>");
        } else if (!emailConfig.isAuthenticationConfigured()) {
            out.println("<div class='error'>❌ L'authentification SMTP n'est PAS configurée</div>");
            out.println("<p>Configurez :</p>");
            out.println("<ul>");
            out.println("<li><code>-Dcolorrun.email.username=votre-email@gmail.com</code></li>");
            out.println("<li><code>-Dcolorrun.email.password=votre-mot-de-passe-app</code></li>");
            out.println("</ul>");
        } else {
            out.println("<div class='success'>✅ Configuration de base OK</div>");
        }
        
        out.println("</div>");
        
        // Formulaire de test
        if (emailConfig.isEnabled() && emailConfig.isAuthenticationConfigured()) {
            out.println("<h2>📧 Test d'envoi</h2>");
            out.println("<form method='post' class='form'>");
            out.println("<label>Email de test :</label><br>");
            out.println("<input type='email' name='testEmail' placeholder='test@example.com' required><br><br>");
            out.println("<input type='submit' value='Envoyer email de test'>");
            out.println("</form>");
        }
        
        // Instructions
        out.println("<h2>📖 Instructions de configuration</h2>");
        out.println("<div class='config'>");
        out.println("<h3>Pour Gmail :</h3>");
        out.println("<ol>");
        out.println("<li>Activez l'authentification à 2 facteurs sur votre compte Gmail</li>");
        out.println("<li>Générez un mot de passe d'application (Google → Sécurité → Mots de passe d'application)</li>");
        out.println("<li>Redémarrez l'application avec :</li>");
        out.println("</ol>");
        out.println("<pre>");
        out.println("mvn clean package tomcat7:run \\");
        out.println("  -Dcolorrun.email.enabled=true \\");
        out.println("  -Dcolorrun.email.username=votre-email@gmail.com \\");
        out.println("  -Dcolorrun.email.password=votre-mot-de-passe-app");
        out.println("</pre>");
        out.println("</div>");
        
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String testEmail = req.getParameter("testEmail");
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Résultat Test Email</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".success { color: green; background: #d4edda; padding: 15px; border-radius: 5px; }");
        out.println(".error { color: red; background: #f8d7da; padding: 15px; border-radius: 5px; }");
        out.println("</style></head><body>");
        
        out.println("<h1>📧 Résultat du test d'envoi</h1>");
        
        try {
            // Test de la connexion
            boolean connectionOk = emailConfig.testConnection();
            if (!connectionOk) {
                throw new Exception("Impossible de se connecter au serveur SMTP");
            }
            
            // Envoi d'un email de test
            boolean success = emailService.testEmailConfiguration(testEmail);
            
            if (success) {
                out.println("<div class='success'>");
                out.println("✅ <strong>Email envoyé avec succès !</strong><br>");
                out.println("Vérifiez votre boîte mail (et le dossier spam) : " + testEmail);
                out.println("</div>");
            } else {
                out.println("<div class='error'>");
                out.println("❌ <strong>Échec de l'envoi</strong><br>");
                out.println("L'email n'a pas pu être envoyé. Vérifiez la configuration SMTP.");
                out.println("</div>");
            }
            
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("❌ <strong>Erreur :</strong> " + e.getMessage() + "<br>");
            out.println("<br><strong>Causes possibles :</strong>");
            out.println("<ul>");
            out.println("<li>Identifiants SMTP incorrects</li>");
            out.println("<li>Mot de passe d'application invalide</li>");
            out.println("<li>Authentification à 2 facteurs non activée sur Gmail</li>");
            out.println("<li>Pare-feu bloquant le port 587</li>");
            out.println("</ul>");
            out.println("</div>");
        }
        
        out.println("<br><a href='/runton-color/test-email'>← Retour au test</a>");
        out.println("</body></html>");
    }
} 