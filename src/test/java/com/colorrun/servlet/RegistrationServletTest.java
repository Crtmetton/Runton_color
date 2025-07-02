package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServletTest {

    RegistrationServlet servlet;
    UserService userServiceMock;
    HttpServletRequest req;
    HttpServletResponse resp;
    RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserService.class);
        servlet = new RegistrationServlet() {{
            this.userService = userServiceMock; // injection du mock
        }};

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(req.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void doGet_forwardsToRegisterPage() throws ServletException, IOException {
        servlet.doGet(req, resp);

        verify(req).getRequestDispatcher("/WEB-INF/views/register.html");
        verify(dispatcher).forward(req, resp);
    }

    @Test
    void doPost_missingFields_forwardsWithError() throws ServletException, IOException {
        when(req.getParameter("firstName")).thenReturn("John");
        when(req.getParameter("lastName")).thenReturn("");
        when(req.getParameter("email")).thenReturn("john@example.com");
        when(req.getParameter("password")).thenReturn("pass123");

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("error"), eq("All fields are required"));
        verify(req).getRequestDispatcher("/WEB-INF/views/register.html");
        verify(dispatcher).forward(req, resp);
        verifyNoInteractions(userServiceMock);
    }

    @Test
    void doPost_successfulRegistration_forwardsToSuccess() throws Exception {
        when(req.getParameter("firstName")).thenReturn("John");
        when(req.getParameter("lastName")).thenReturn("Doe");
        when(req.getParameter("email")).thenReturn("john@example.com");
        when(req.getParameter("password")).thenReturn("pass123");

        when(req.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/register"));

        // Pas besoin de simuler userService.register, car void sans exception
        doNothing().when(userServiceMock).register(any(User.class));
        doNothing().when(userServiceMock).sendVerificationEmail(any(User.class), anyString());

        servlet.doPost(req, resp);

        verify(userServiceMock).register(any(User.class));
        verify(userServiceMock).sendVerificationEmail(any(User.class), eq("http://localhost:8080"));

        verify(req).setAttribute("success", "Registration successful. Please check your email to verify your account.");
        verify(req).getRequestDispatcher("/WEB-INF/views/register-success.html");
        verify(dispatcher).forward(req, resp);
    }

    @Test
    void doPost_serviceThrowsException_forwardsWithError() throws Exception {
        when(req.getParameter("firstName")).thenReturn("John");
        when(req.getParameter("lastName")).thenReturn("Doe");
        when(req.getParameter("email")).thenReturn("john@example.com");
        when(req.getParameter("password")).thenReturn("pass123");

        when(req.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/register"));

        doThrow(new RuntimeException("DB error")).when(userServiceMock).register(any(User.class));

        servlet.doPost(req, resp);

        verify(req).setAttribute("error", "DB error");
        verify(req).getRequestDispatcher("/WEB-INF/views/register.html");
        verify(dispatcher).forward(req, resp);
    }
}
