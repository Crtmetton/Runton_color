package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet de gestion du profil utilisateur (<code>/profile</code>).
 * <p>
 * Fonctionnalités :
 * <ul>
 *   <li>Affichage du profil de l'utilisateur connecté (GET)</li>
 *   <li>Mise à jour des informations de profil (nom, prénom) et changement
 *       de mot de passe (POST)</li>
 *   <li>Redirection vers la page de connexion si l'utilisateur n'est pas
 *       authentifié</li>
 * </ul>
 * Les méthodes lèvent {@link javax.servlet.ServletException} et
 * {@link java.io.IOException} comme requis par l'API Servlet.
 * </p>
 */
@WebServlet(name = "ProfileServlet", urlPatterns = "/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl();

    /**
     * Affiche la page de profil.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    /**
     * Traite la mise à jour du profil : noms et mot de passe.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String currentPassword = req.getParameter("currentPassword");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (firstName != null) user.setFirstName(firstName.trim());
        if (lastName != null) user.setLastName(lastName.trim());

        try {
            userService.updateProfile(user); // update names

            if (password != null && !password.isEmpty()) {
                if (!password.equals(confirmPassword)) {
                    req.setAttribute("error", "Les mots de passe ne correspondent pas.");
                    req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
                    return;
                }
                if (!userService.changePassword(user.getId(), currentPassword, password)) {
                    req.setAttribute("error", "Ancien mot de passe incorrect.");
                    req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
                    return;
                }
            }
            session.setAttribute("user", user);
            req.setAttribute("success", "Profil mis à jour avec succès.");
        } catch (SQLException | IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }
} 