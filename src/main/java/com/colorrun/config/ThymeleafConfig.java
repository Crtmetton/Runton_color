package com.colorrun.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;
import javax.servlet.ServletContext;

public class ThymeleafConfig {
    
    private static TemplateEngine templateEngine;
    
    public static void init(ServletContext servletContext) {
        // Create web application
        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
        
        // Create template resolver
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setCharacterEncoding("UTF-8");
        
        // Create template engine
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }
    
    public static TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
} 