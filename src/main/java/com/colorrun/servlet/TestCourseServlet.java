package com.colorrun.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.business.Course;

/**
 * Servlet de test simple pour les courses
 */
public class TestCourseServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Test Courses</title></head><body>");
            out.println("<h1>Test des Courses</h1>");
            
            // Test du service
            CourseService courseService = new CourseServiceImpl();
            
            out.println("<p>Test du service...</p>");
            
            // Test findAll
            var courses = courseService.findAll();
            out.println("<p>Nombre de courses trouvées : " + courses.size() + "</p>");
            
            // Affichage des courses
            out.println("<ul>");
            for (Course course : courses) {
                out.println("<li>");
                out.println("ID: " + course.getId() + " - ");
                out.println("Nom: " + course.getName() + " - ");
                out.println("Ville: " + course.getCity() + " - ");
                out.println("Distance: " + course.getDistance() + " km");
                out.println("</li>");
            }
            out.println("</ul>");
            
            out.println("</body></html>");
            
        } catch (Exception e) {
            out.println("<h2>Erreur : " + e.getClass().getSimpleName() + "</h2>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
    }
} 