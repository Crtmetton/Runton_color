package com.colorrun.util;

import com.colorrun.config.ThymeleafConfig;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Utilitaire pour rendre les templates Thymeleaf
 * Remplace les forwards JSP par du rendu HTML dynamique
 */
public class ThymeleafUtil {
    
    /**
     * Rend un template Thymeleaf avec les données fournies
     * 
     * @param templateName Nom du template (sans .html)
     * @param request Requête HTTP
     * @param response Réponse HTTP
     * @param variables Variables à passer au template
     */
    public static void render(String templateName, 
                            HttpServletRequest request, 
                            HttpServletResponse response,
                            Map<String, Object> variables) throws ServletException, IOException {
        
        Logger.step("ThymeleafUtil", "Rendu template: " + templateName);
        
        try {
            // Récupérer le moteur Thymeleaf
            TemplateEngine templateEngine = ThymeleafConfig.getTemplateEngine();
            
            if (templateEngine == null) {
                Logger.error("ThymeleafUtil", "❌ Moteur Thymeleaf non disponible");
                throw new ServletException("Thymeleaf non initialisé");
            }
            
            // Créer le contexte web
            JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(
                request.getServletContext()
            );
            WebContext context = new WebContext(application.buildExchange(request, response));
            
            // Ajouter les variables du système de token (automatiques)
            addTokenVariables(context, request);
            
            // Ajouter les variables personnalisées
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    context.setVariable(entry.getKey(), entry.getValue());
                    Logger.debug("ThymeleafUtil", "Variable: " + entry.getKey() + " = " + entry.getValue());
                }
            }
            
            // Configurer la réponse
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            
            // Rendre le template
            templateEngine.process(templateName, context, response.getWriter());
            
            Logger.success("ThymeleafUtil", "✅ Template " + templateName + " rendu");
            
        } catch (Exception e) {
            Logger.error("ThymeleafUtil", "❌ Erreur rendu template " + templateName, e);
            throw new ServletException("Erreur rendu template: " + templateName, e);
        }
    }
    
    /**
     * Ajoute automatiquement les variables du système de token
     */
    private static void addTokenVariables(WebContext context, HttpServletRequest request) {
        // Ces variables sont déjà ajoutées par AuthenticationFilter
        // On les récupère depuis les attributs de la requête
        
        Object isAuthenticated = request.getAttribute("isAuthenticated");
        Object userName = request.getAttribute("userName");
        Object userRole = request.getAttribute("userRole");
        Object isAdmin = request.getAttribute("isAdmin");
        Object isOrganizer = request.getAttribute("isOrganizer");
        Object isUser = request.getAttribute("isUser");
        Object userToken = request.getAttribute("userToken");
        
        // Ajouter au contexte Thymeleaf
        context.setVariable("isAuthenticated", isAuthenticated != null ? isAuthenticated : false);
        context.setVariable("userName", userName != null ? userName : "");
        context.setVariable("userRole", userRole != null ? userRole : "");
        context.setVariable("isAdmin", isAdmin != null ? isAdmin : false);
        context.setVariable("isOrganizer", isOrganizer != null ? isOrganizer : false);
        context.setVariable("isUser", isUser != null ? isUser : false);
        context.setVariable("userToken", userToken);
        
        Logger.debug("ThymeleafUtil", "Variables token ajoutées au contexte Thymeleaf");
    }
    
    /**
     * Rend un template simple sans variables supplémentaires
     */
    public static void render(String templateName, 
                            HttpServletRequest request, 
                            HttpServletResponse response) throws ServletException, IOException {
        render(templateName, request, response, null);
    }
} 