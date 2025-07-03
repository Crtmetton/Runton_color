package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthServletTest {

    private AuthServlet servlet;
    private UserService mockUserService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        mockUserService = mock(UserService.class);

        // Instanciation du servlet avec constructeur par défaut
        servlet = new AuthServlet();

        // Injection par réflexion dans le champ private final userService
        Field userServiceField = AuthServlet.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);

        // Retirer le 'final' (optionnel, en fonction de la JVM, sinon peut ignorer)
        // Pour Java 12+, cela peut fonctionner directement avec setAccessible(true) et set
        userServiceField.set(servlet, mockUserService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/views/acceuil-2.html");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_AdminSuccess() throws Exception {
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");

        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("pass");
        when(mockUserService.login("admin@test.com", "pass")).thenReturn(Optional.of(admin));

        servlet.doPost(request, response);

        verify(session).setAttribute("user", admin);
        verify(response).sendRedirect("/app/admin/requests");
    }

    @Test
    void testDoPost_OrganizerSuccess() throws Exception {
        User organizer = new User();
        organizer.setEmail("org@test.com");
        organizer.setRole("ORGANIZER");

        when(request.getParameter("email")).thenReturn("org@test.com");
        when(request.getParameter("password")).thenReturn("pass");
        when(mockUserService.login("org@test.com", "pass")).thenReturn(Optional.of(organizer));

        servlet.doPost(request, response);

        verify(session).setAttribute("user", organizer);
        verify(response).sendRedirect("/app/organizer/courses");
    }

    @Test
    void testDoPost_RegularUserSuccess() throws Exception {
        User user = new User();
        user.setEmail("user@test.com");
        user.setRole("USER");

        when(request.getParameter("email")).thenReturn("user@test.com");
        when(request.getParameter("password")).thenReturn("pass");
        when(mockUserService.login("user@test.com", "pass")).thenReturn(Optional.of(user));

        servlet.doPost(request, response);

        verify(session).setAttribute("user", user);
        verify(response).sendRedirect("/app/courses");
    }

    @Test
    void testDoPost_InvalidCredentials() throws Exception {
        when(request.getParameter("email")).thenReturn("wrong@test.com");
        when(request.getParameter("password")).thenReturn("wrongpass");
        when(mockUserService.login("wrong@test.com", "wrongpass")).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), eq("Invalid email or password"));
        verify(request).getRequestDispatcher("/WEB-INF/views/login.html");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_Exception() throws Exception {
        when(request.getParameter("email")).thenReturn("error@test.com");
        when(request.getParameter("password")).thenReturn("pass");
        when(mockUserService.login(any(), any())).thenThrow(new RuntimeException("DB down"));

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), eq("DB down"));
        verify(request).getRequestDispatcher("/WEB-INF/views/login.html");
        verify(dispatcher).forward(request, response);
    }
}
