package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.CourseService;
import com.colorrun.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;

import static org.mockito.Mockito.*;

class CourseDetailServletTest {

    private CourseDetailServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        servlet = new CourseDetailServlet() {
            @Override
            protected CourseService getCourseService() {
                return null;
            }

            @Override
            protected MessageService getMessageService() {
                return null;
            }
        }; // instance normale avec services internes

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
    }

    @Test
    void doGet_NoIdParameter_ShouldReturnBadRequest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
    }

    @Test
    void doGet_InvalidIdParameter_ShouldReturnBadRequest() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
    }

    @Test
    void doPost_UserNotLoggedIn_ShouldReturnUnauthorized() throws ServletException, IOException {
        when(request.getSession().getAttribute("user")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to post messages");
    }

    @Test
    void doPost_MissingParameters_ShouldReturnBadRequest() throws ServletException, IOException {
        User user = new User();
        user.setId(10);

        when(request.getSession().getAttribute("user")).thenReturn(user);
        when(request.getParameter("courseId")).thenReturn(null);
        when(request.getParameter("content")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID and content are required");
    }

    @Test
    void doPost_InvalidCourseId_ShouldReturnBadRequest() throws ServletException, IOException {
        User user = new User();
        user.setId(10);

        when(request.getSession().getAttribute("user")).thenReturn(user);
        when(request.getParameter("courseId")).thenReturn("abc");
        when(request.getParameter("content")).thenReturn("Hello");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
    }
}
