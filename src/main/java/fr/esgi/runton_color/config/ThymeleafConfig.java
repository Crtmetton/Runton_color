package fr.esgi.runton_color.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebListener
public class ThymeleafConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        TemplateEngine templateEngine = templateEngine(servletContext);
        servletContext.setAttribute("templateEngine", templateEngine);

        System.out.println(">>> ThymeleafConfig: TemplateEngine configuré et stocké dans le ServletContext");
    }

    private TemplateEngine templateEngine(ServletContext servletContext) {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver(servletContext));
        return templateEngine;
    }

    private ITemplateResolver templateResolver(ServletContext servletContext) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Rien à faire ici
    }
}
