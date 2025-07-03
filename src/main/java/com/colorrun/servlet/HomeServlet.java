package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.business.Course;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.colorrun.config.ThymeleafConfig;

public class HomeServlet extends HttpServlet {
    
    private final CourseService courseService;


    public HomeServlet() {
        this.courseService = new CourseServiceImpl();
    }

    // constructeur pour tests
    public HomeServlet(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Récupérer les prochaines courses (limité à 3)
            List<Course> upcomingCourses = courseService.findFiltered(null, null, null, "date");
            if (upcomingCourses.size() > 3) {
                upcomingCourses = upcomingCourses.subList(0, 3);
            }

            // Configurer Thymeleaf
            TemplateEngine engine = ThymeleafConfig.getTemplateEngine();
            Context context = new Context();

            // Ajouter les données au contexte
            context.setVariable("upcomingCourses", upcomingCourses);

            // Rendre le template
            resp.setContentType("text/html;charset=UTF-8");
            engine.process("acceuil-2", context, resp.getWriter());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
} 