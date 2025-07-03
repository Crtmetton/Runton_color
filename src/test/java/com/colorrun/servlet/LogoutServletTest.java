package com.colorrun.servlet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

class LogoutServletTest {

    @Test
    void testDoGet_withSession_shouldInvalidateAndRedirect() throws IOException, ServletException {
        // Mock des objets HttpServletRequest, HttpServletResponse, HttpSession
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        // Configurer les mocks
        when(req.getSession(false)).thenReturn(session);
        when(req.getContextPath()).thenReturn("/app");

        // Instancier la servlet
        LogoutServlet servlet = new LogoutServlet();

        // Appeler la méthode à tester
        servlet.doGet(req, resp);

        // Vérifier que session.invalidate() a été appelé
        verify(session, times(1)).invalidate();

        // Vérifier la redirection vers "/app/"
        verify(resp).sendRedirect("/app/");
    }

    @Test
    void testDoGet_withoutSession_shouldRedirect() throws IOException, ServletException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getSession(false)).thenReturn(null);
        when(req.getContextPath()).thenReturn("/app");

        LogoutServlet servlet = new LogoutServlet();

        servlet.doGet(req, resp);

        // Comme il n'y a pas de session, invalidate ne peut pas être appelé
        // On vérifie uniquement la redirection
        verify(resp).sendRedirect("/app/");
    }
}
