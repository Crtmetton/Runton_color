package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import com.colorrun.service.CourseService;
import com.colorrun.service.MessageService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.colorrun.service.impl.MessageServiceImpl;
import com.colorrun.business.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public abstract class CourseDetailServlet extends HttpServlet {

    private final CourseService courseService;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    public CourseDetailServlet() {
        this.courseService = new CourseServiceImpl();
        this.messageService = new MessageServiceImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            courseService.findById(id).ifPresentOrElse(course -> {
                try {
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("course", course);
                    responseData.put("messages", messageService.findByCourse(id));

                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getOutputStream(), responseData);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                try {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to post messages");
            return;
        }

        String courseIdParam = req.getParameter("courseId");
        String content = req.getParameter("content");

        if (courseIdParam == null || courseIdParam.isEmpty() || content == null || content.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID and content are required");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            messageService.create(courseId, user.getId(), content);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    protected abstract CourseService getCourseService();

    protected abstract MessageService getMessageService();
}