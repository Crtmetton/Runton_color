package com.colorrun.servlet;

import com.colorrun.business.User;
import com.colorrun.service.ParticipationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

class ParticipationServletTest {

    ParticipationServlet servlet;
    ParticipationService serviceMock;
    HttpServletRequest req;
    HttpServletResponse resp;
    HttpSession session;
    ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        serviceMock = mock(ParticipationService.class);
        servlet = new ParticipationServlet() {
            {
                this.participationService = serviceMock;
            }
        };

        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);

        outputStream = new ByteArrayOutputStream();
        try {
            when(resp.getOutputStream()).thenReturn(new ServletOutputStream() {
                @Override
                public boolean isReady() { return true; }
                @Override
                public void setWriteListener(javax.servlet.WriteListener listener) {}
                @Override
                public void write(int b) throws IOException { outputStream.write(b); }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Tests doPost ---

    @Test
    void doPost_userNotLogged_sendsUnauthorized() throws IOException, ServletException {
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to register for a course");
    }

    @Test
    void doPost_missingCourseId_sendsBadRequest() throws IOException, ServletException {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("courseId")).thenReturn(null);

        servlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
    }

    @Test
    void doPost_invalidCourseId_sendsBadRequest() throws IOException, ServletException {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("courseId")).thenReturn("abc");

        servlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
    }

    @Test
    void doPost_success_writesPdf() throws Exception {
        User user = new User();
        user.setId(7);
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("courseId")).thenReturn("123");

        when(serviceMock.register(7, 123)).thenReturn(55);
        byte[] fakePdf = new byte[]{0x01, 0x02, 0x03};
        when(serviceMock.generateBibPdf(55)).thenReturn(fakePdf);

        servlet.doPost(req, resp);

        verify(resp).setContentType("application/pdf");
        verify(resp).setHeader("Content-Disposition", "attachment; filename=dossard_55.pdf");
        // Vérifier que les bytes PDF ont été écrits dans la sortie
        assertArrayEquals(fakePdf, outputStream.toByteArray());
    }

    @Test
    void doPost_serviceThrowsInternalError() throws Exception {
        User user = new User();
        user.setId(7);
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("courseId")).thenReturn("123");

        doThrow(new RuntimeException("Service failed")).when(serviceMock).register(7, 123);

        servlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service failed");
    }

    // --- Tests doDelete ---

    @Test
    void doDelete_userNotLogged_sendsUnauthorized() throws IOException, ServletException {
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to cancel a registration");
    }

    @Test
    void doDelete_missingParticipationId_sendsBadRequest() throws IOException, ServletException {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
                when(req.getParameter("id")).thenReturn(null);

        servlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Participation ID is required");
    }

    @Test
    void doDelete_invalidParticipationId_sendsBadRequest() throws IOException, ServletException {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("id")).thenReturn("notanumber");

        servlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid participation ID");
    }

    @Test
    void doDelete_success_returnsNoContent() throws Exception {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("id")).thenReturn("456");

        // Pas besoin de simuler de résultat particulier, on teste juste l'appel
        doNothing().when(serviceMock).cancel(456);

        servlet.doDelete(req, resp);

        verify(serviceMock).cancel(456);
        verify(resp).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDelete_serviceThrowsInternalError() throws Exception {
        User user = new User();
        when(session.getAttribute("user")).thenReturn(user);
        when(req.getParameter("id")).thenReturn("456");

        doThrow(new RuntimeException("Cancel failed")).when(serviceMock).cancel(456);

        servlet.doDelete(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cancel failed");
    }
}
