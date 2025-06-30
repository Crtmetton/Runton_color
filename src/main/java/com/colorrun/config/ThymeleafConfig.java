package com.colorrun.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;
import com.colorrun.util.Logger;

import javax.servlet.ServletContext;

/**
 * Configuration Thymeleaf pour remplacer les JSP
 * Utilise des templates HTML avec données dynamiques
 */
public class ThymeleafConfig {
    
    private static TemplateEngine templateEngine;
    
    /**
     * Initialise Thymeleaf pour remplacer les JSP
     */
    public static void init(ServletContext servletContext) {
        Logger.separator("CONFIGURATION THYMELEAF");
        Logger.step("ThymeleafConfig", "Initialisation du moteur de template");
        
        try {
            // Create web application
            JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
            
            // Create template resolver
            WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
            templateResolver.setPrefix("/WEB-INF/templates/"); // Changement vers /templates/
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setCacheable(false); // Pas de cache en dev
            templateResolver.setCharacterEncoding("UTF-8");
            
            Logger.info("ThymeleafConfig", "📁 Templates: /WEB-INF/templates/*.html");
            Logger.info("ThymeleafConfig", "🔤 Encodage: UTF-8");
            Logger.info("ThymeleafConfig", "🚫 Cache: désactivé");
            
            // Create template engine
            templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            
            Logger.success("ThymeleafConfig", "✅ Thymeleaf configuré avec succès");
            Logger.stepSuccess("ThymeleafConfig", "Moteur prêt à remplacer les JSP");
            
        } catch (Exception e) {
            Logger.error("ThymeleafConfig", "❌ Erreur configuration Thymeleaf", e);
        }
    }
    
    /**
     * Retourne le moteur de template
     */
    public static TemplateEngine getTemplateEngine() {
        if (templateEngine == null) {
            Logger.warn("ThymeleafConfig", "⚠️ Thymeleaf non initialisé !");
        }
        return templateEngine;
    }
    
    /**
     * Vérifie si Thymeleaf est prêt
     */
    public static boolean isReady() {
        return templateEngine != null;
    }
} 