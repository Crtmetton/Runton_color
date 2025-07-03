package com.colorrun.servlet;

import com.colorrun.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

import static org.mockito.Mockito.*;

class EmailVerificationServletTest {

    @Mock private UserService userService;
    @InjectMocks private EmailVerificationServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenTokenIsValid_shouldSetMessageAndForward() throws Exception {
        String token = "validToken";
        when(request.getParameter("token")).thenReturn(token);
        when(userService.verifyEmail(token)).thenReturn(true);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("message"), contains("activé avec succès"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void whenTokenIsMissing_shouldSendBadRequest() throws Exception {
        when(request.getParameter("token")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is required");
    }

    @Test
    void whenTokenIsInvalid_shouldSetErrorAndForward() throws Exception {
        String token = "invalidToken";
        when(request.getParameter("token")).thenReturn(token);
        when(userService.verifyEmail(token)).thenReturn(false);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("error"), contains("invalide ou a expiré"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void whenExceptionThrown_shouldSendServerError() throws Exception {
        when(request.getParameter("token")).thenReturn("token");
        when(userService.verifyEmail(any())).thenThrow(new RuntimeException("boom"));

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), contains("Erreur interne"));
    }
}
