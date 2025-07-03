package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.service.CourseService;
import com.colorrun.service.impl.CourseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CourseListServletTest {

    @Test
    void testDoGet_WithRealService_NoException() throws Exception {
        // Préparer mocks HTTP
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Flux de sortie simulé
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override public void write(int b) { baos.write(b); }
            @Override public boolean isReady() { return true; }
            @Override public void setWriteListener(WriteListener listener) {}
        };

        when(request.getParameter("date")).thenReturn(null);
        when(request.getParameter("city")).thenReturn(null);
        when(request.getParameter("distance")).thenReturn(null);
        when(request.getParameter("sort")).thenReturn(null);

        when(response.getOutputStream()).thenReturn(servletOutputStream);

        // Création de la servlet (avec implémentation réelle du service)
        CourseListServlet servlet = new CourseListServlet();

        // Appel de la méthode testée
        servlet.doGet(request, response);

        // Vérifier que le content type JSON est bien défini
        verify(response).setContentType("application/json");

        // Vérifier qu’il y a bien une sortie JSON non vide
        String jsonOutput = baos.toString();
        assertFalse(jsonOutput.isEmpty());
    }
}
