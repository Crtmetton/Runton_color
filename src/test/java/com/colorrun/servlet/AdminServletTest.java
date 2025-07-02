package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.OrganizerRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.*;

class AdminServletTest {

    private AdminServlet servlet;
    private OrganizerRequestService mockService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        // Crée une instance réelle
        servlet = new AdminServlet();

        // Crée un mock pour le service
        mockService = mock(OrganizerRequestService.class);

        // Injecte le mock dans le champ privé via réflexion
        Field field = AdminServlet.class.getDeclaredField("organizerRequestService");
        field.setAccessible(true);
        field.set(servlet, mockService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet_NotLoggedIn() throws ServletException, IOException {
        when(session.getAttribute("user")).thenReturn(null);
        servlet.doGet(request, response);
        verify(response).sendRedirect("/app/login");
    }

    @Test
    void testDoGet_NotAdmin() throws ServletException, IOException {
        User user = new User();
        user.setRole("USER");
        when(session.getAttribute("user")).thenReturn(user);
        servlet.doGet(request, response);
        verify(response).sendRedirect("/app/login");
    }

    @Test
    void testDoGet_AsAdmin() throws ServletException, IOException, SQLException {
        User admin = new User();
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);
        when(mockService.findPending()).thenReturn(List.of());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("requests"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_Exception() throws ServletException, IOException, SQLException {
        User admin = new User();
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);
        when(mockService.findPending()).thenThrow(new RuntimeException("DB error"));

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), contains("DB error"));
    }

    @Test
    void testDoPost_NotLoggedIn() throws ServletException, IOException {
        when(session.getAttribute("user")).thenReturn(null);
        servlet.doPost(request, response);
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
    }

    @Test
    void testDoPost_NotAdmin() throws ServletException, IOException {
        User user = new User();
        user.setRole("USER");
        when(session.getAttribute("user")).thenReturn(user);
        servlet.doPost(request, response);
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
    }

    @Test
    void testDoPost_Approve() throws ServletException, IOException, SQLException {
        User admin = new User();
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);
        when(request.getParameter("requestId")).thenReturn("42");
        when(request.getParameter("action")).thenReturn("approve");

        servlet.doPost(request, response);

        verify(mockService).approve(42);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoPost_Reject() throws ServletException, IOException, SQLException {
        User admin = new User();
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);
        when(request.getParameter("requestId")).thenReturn("77");
        when(request.getParameter("action")).thenReturn("reject");

        servlet.doPost(request, response);

        verify(mockService).reject(77);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoPost_Exception() throws ServletException, IOException, SQLException {
        User admin = new User();
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);
        when(request.getParameter("requestId")).thenReturn("99");
        when(request.getParameter("action")).thenReturn("approve");
        doThrow(new RuntimeException("Something broke")).when(mockService).approve(99);

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), contains("Something broke"));
    }
}
