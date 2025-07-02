package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.business.User;
import com.colorrun.service.ParticipationService;
import com.colorrun.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServletTest {

    private UserService userService;
    private ParticipationService participationService;
    private ProfileServlet servlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private ByteArrayOutputStream responseOutputStream;

    @BeforeEach
    void setUp() throws Exception {
        userService = mock(UserService.class);
        participationService = mock(ParticipationService.class);

        servlet = new ProfileServlet() {
            {
                // Injection des mocks dans le servlet (via reflection ou setter si existant)
                this.userService = ProfileServletTest.this.userService;
                this.participationService = ProfileServletTest.this.participationService;
                this.objectMapper = new ObjectMapper();
            }
        };

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);

        responseOutputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new javax.servlet.ServletOutputStream() {
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(javax.servlet.WriteListener writeListener) {}
            @Override
            public void write(int b) {
                responseOutputStream.write(b);
            }
        });
    }
    private User createTestUser() {
        User user = new User();
        user.setId(42);
        user.setFirstName("Jean");
        user.setLastName("Dupont");
        user.setPhotoUrl("https://example.com/photo.jpg");
        return user;
    }

    private List<Course> createTestCourses() {
        return List.of(
                new Course(1, "Course A", "desc A", LocalDateTime.now().plusDays(1), "Paris", 10.0, 100, "Cause A"),
                new Course(2, "Course B", "desc B", LocalDateTime.now().plusDays(2), "Lyon", 5.0, 50, "Cause B")
        );
    }

    @Test
    void doGet_whenUserInSession_shouldReturnUserAndCoursesJson() throws Exception {
        // Given : mock request, response, session, service etc.
        when(request.getSession(false)).thenReturn(session);
        User testUser = createTestUser();
        List<Course> testCourses = createTestCourses();
        when(session.getAttribute("user")).thenReturn(testUser);
        when(participationService.findCoursesByUser(testUser.getId())).thenReturn(testCourses);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;  // plutôt true pour indiquer prêt à écrire
            }
            @Override
            public void setWriteListener(WriteListener writeListener) {}

            @Override
            public void write(int b) throws IOException {
                baos.write(b);
            }

            @Override
            public void flush() throws IOException {
                baos.flush();
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // When
        servlet.doGet(request, response);

        // Important: flush the stream to ensure all data is written
        servletOutputStream.flush();

        String jsonResponse = baos.toString(StandardCharsets.UTF_8);
        System.out.println("JSON Response: " + jsonResponse);

        // Then
        assertTrue(jsonResponse.contains("\"firstName\":\"Jean\""));
        assertTrue(jsonResponse.contains("\"name\":\"Course A\""));
        assertTrue(jsonResponse.contains("\"name\":\"Course B\""));
    }


    @Test
    void doGet_whenNoUserInSession_shouldReturnUnauthorized() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to view your profile");
    }

    @Test
    void doPost_whenUserInSession_shouldUpdateUserProfile() throws Exception {
        User user = new User();
        user.setId(123);
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");
        user.setPasswordHash("oldpass");
        user.setPhotoUrl("oldPhoto.jpg");

        when(session.getAttribute("user")).thenReturn(user);
        when(request.getParameter("firstName")).thenReturn("NewFirst");
        when(request.getParameter("lastName")).thenReturn("NewLast");
        when(request.getParameter("password")).thenReturn("newpass");
        when(request.getParameter("photoUrl")).thenReturn("newPhoto.jpg");

        servlet.doPost(request, response);

        // Vérifie que le user a bien été modifié
        assertEquals("NewFirst", user.getFirstName());
        assertEquals("NewLast", user.getLastName());
        assertEquals("newpass", user.getPasswordHash());
        assertEquals("newPhoto.jpg", user.getPhotoUrl());

        // Vérifie que la mise à jour a été appelée
        verify(userService).updateProfile(user);

        // Vérifie que la session a été mise à jour avec le user modifié
        verify(session).setAttribute("user", user);

        // Vérifie le code HTTP 204 No Content
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doPost_whenNoUserInSession_shouldReturnUnauthorized() throws Exception {
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to update your profile");
    }
}
