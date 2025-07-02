package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.UserService;
import com.colorrun.service.impl.UserServiceImpl;
import com.colorrun.service.impl.ParticipationServiceImpl;
import com.colorrun.business.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProfileServlet extends HttpServlet {

UserService userService;
ParticipationService participationService;
ObjectMapper objectMapper;

public ProfileServlet() {
    this.userService = new UserServiceImpl();
    this.participationService = new ParticipationServiceImpl();
    this.objectMapper = new ObjectMapper();
}

@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    User user = (User) req.getSession().getAttribute("user");
    if (user == null) {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to view your profile");
        return;
    }

    try {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("user", user);
        responseData.put("courses", participationService.findCoursesByUser(user.getId()));

        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getOutputStream(), responseData);
    } catch (Exception e) {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
}

@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    User user = (User) req.getSession().getAttribute("user");
    if (user == null) {
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to update your profile");
        return;
    }

    String firstName = req.getParameter("firstName");
    String lastName = req.getParameter("lastName");
    String password = req.getParameter("password");
    String photoUrl = req.getParameter("photoUrl");

    try {
        // Update only provided fields
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }

        if (photoUrl != null && !photoUrl.isEmpty()) {
            user.setPhotoUrl(photoUrl);
        }

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(password);
        }

        userService.updateProfile(user);

        // Update session with updated user
        req.getSession().setAttribute("user", user);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } catch (Exception e) {
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
}
