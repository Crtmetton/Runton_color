package fr.esgi.runton_color.servlet;

import fr.esgi.runton_color.buisness.Utilisateur;
import fr.esgi.runton_color.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Enumeration;

@WebServlet(name = "inscriptionServlet", value = "/inscription")
public class RegisterServlet{


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Création du contexte Thymeleaf
        Context context = new Context();

        // Traitement de la page
        renderTemplate(request, response, "auth/inscription", context);
    }

    private void renderTemplate(HttpServletRequest request, HttpServletResponse response, String s, Context context) {
    }
}