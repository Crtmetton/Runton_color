package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.OrganizerRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizerRequestServletTest {

    OrganizerRequestServlet servlet;
    OrganizerRequestService serviceMock;
    HttpServletRequest req;
    HttpServletResponse resp;
    HttpSession session;
    RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        serviceMock = mock(OrganizerRequestService.class);
        servlet = new OrganizerRequestServlet() {
            // Inject mock service
            {
                // on override du constructeur
                this.organizerRequestService = serviceMock;
            }
        };

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(req.getSession()).thenReturn(session);
    }

    // --- Tests doGet ---

    @Test
    void doGet_userNotLogged_redirectsToLogin() throws IOException, ServletException {
        when(session.getAttribute("user")).thenReturn(null);
        when(req.getContextPath()).thenReturn("/app");

        servlet.doGet(req, resp);

        verify(resp).sendRedirect("/app/login");
    }

    @Test
    void doGet_userLoggedForwardsWithHasRequestTrue() throws Exception {
        User user = new User();
        user.setId(123);
        when(session.getAttribute("user")).thenReturn(user);
        when(serviceMock.hasRequestForUser(123)).thenReturn(true);
        when(req.getRequestDispatcher("/WEB-INF/views/organizer-request.html")).thenReturn(dispatcher);

        servlet.doGet(req, resp);

        verify(req).setAttribute("hasRequest", true);
        verify(dispatcher).forward(req, resp);
    }

    @Test
    void doGet_serviceThrowsError_sendsError() throws Exception {
        User user = new User();
        user.setId(123);
        when(session.getAttribute("user")).thenReturn(user);
        when(serviceMock.hasRequestForUser(123)).thenThrow(new RuntimeException("Service failed"));

        servlet.doGet(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service failed");
    }

    // --- Tests doPost ---

    @Test
    void doPost_userNotLogged_sendsUnauthorized() throws IOException, ServletException {
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to submit a request");
    }

    @Test
    void doPost_emptyMotivation_forwardsWithError() throws IOException, ServletException {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("motivation")).thenReturn("   ");
        when(req.getRequestDispatcher("/WEB-INF/views/organizer-request.html")).thenReturn(dispatcher);

        servlet.doPost(req, resp);

        verify(req).setAttribute(eq("error"), anyString());
        verify(dispatcher).forward(req, resp);
    }

    @Test
    void doPost_successfulCreation_forwardsWithSuccess() throws Exception {
        User user = new User();
        user.setId(42);
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("motivation")).thenReturn("I want to organize");
        when(req.getRequestDispatcher("/WEB-INF/views/organizer-request.html")).thenReturn(dispatcher);

        servlet.doPost(req, resp);

        verify(serviceMock).create(42, "I want to organize");
        verify(req).setAttribute("success", "Your request has been submitted successfully");
        verify(req).setAttribute("hasRequest", true);
        verify(dispatcher).forward(req, resp);
    }

    @Test
    void doPost_serviceThrowsError_forwardsWithError() throws Exception {
        User user = new User();
        user.setId(42);
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("motivation")).thenReturn("Valid motivation");
        when(req.getRequestDispatcher("/WEB-INF/views/organizer-request.html")).thenReturn(dispatcher);
        doThrow(new RuntimeException("Create failed")).when(serviceMock).create(42, "Valid motivation");

        servlet.doPost(req, resp);

        verify(req).setAttribute("error", "Create failed");
        verify(dispatcher).forward(req, resp);
    }
}
