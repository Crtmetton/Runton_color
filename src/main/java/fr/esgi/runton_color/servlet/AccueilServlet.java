package fr.esgi.runton_color.servlet;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.esgi.runton_color.service.CourseService;
import fr.esgi.runton_color.service.impl.CourseServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

/**
 * Cette classe aura le pouvoir de traiter des requêtes HTTP
 */
@WebServlet(name = "coursesServlet", value = {"/index", "/courses"})
public class AccueilServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Redirection vers le fichier HTML
        response.sendRedirect("/templates/acceuil.html");
    }
}