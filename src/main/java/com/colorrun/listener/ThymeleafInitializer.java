package com.colorrun.listener;

import com.colorrun.config.ThymeleafConfig;
import com.colorrun.util.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Initialise Thymeleaf au démarrage de l'application
 * Remplace les JSP par des templates HTML dynamiques
 */
@WebListener
public class ThymeleafInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Logger.separator("DÉMARRAGE APPLICATION");
        Logger.step("ThymeleafInitializer", "Initialisation Thymeleaf");
        
        ServletContext servletContext = sce.getServletContext();
        
        // Initialiser Thymeleaf
        ThymeleafConfig.init(servletContext);
        
        if (ThymeleafConfig.isReady()) {
            Logger.success("ThymeleafInitializer", "✅ Thymeleaf prêt - JSP remplacés");
            servletContext.setAttribute("thymeleafReady", true);
        } else {
            Logger.error("ThymeleafInitializer", "❌ Échec initialisation Thymeleaf");
            servletContext.setAttribute("thymeleafReady", false);
        }
        
        Logger.stepSuccess("ThymeleafInitializer", "Application prête avec Thymeleaf");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Logger.info("ThymeleafInitializer", "🔄 Arrêt application Thymeleaf");
    }
} 