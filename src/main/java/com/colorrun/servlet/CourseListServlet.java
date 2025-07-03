package com.colorrun.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CourseListServlet extends HttpServlet {

    private CourseService courseService;
    private ObjectMapper objectMapper;

    public CourseListServlet() {
        this.courseService = new CourseServiceImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String date = req.getParameter("date");
        String city = req.getParameter("city");
        String distance = req.getParameter("distance");
        String sort = req.getParameter("sort");

        try {
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), courseService.findFiltered(date, city, distance, sort));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}