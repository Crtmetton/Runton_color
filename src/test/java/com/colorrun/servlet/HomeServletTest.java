package com.colorrun.servlet;

import com.colorrun.business.Course;
import com.colorrun.config.ThymeleafConfig;
import com.colorrun.service.CourseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class HomeServletTest {

    private CourseService courseService;
    private HomeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        courseService = mock(CourseService.class);
        servlet = new HomeServlet(courseService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void whenMoreThanThreeCourses_shouldTrimListAndRenderTemplate() throws Exception {
        List<Course> courses = List.of(
                new Course(1, "Course 1", "Description 1", LocalDateTime.now().plusDays(1), "Paris", 10.0, 100, "Cause 1"),
                new Course(2, "Course 2", "Description 2", LocalDateTime.now().plusDays(2), "Lyon", 5.0, 50, "Cause 2"),
                new Course(3, "Course 3", "Description 3", LocalDateTime.now().plusDays(3), "Nice", 15.0, 150, "Cause 3"),
                new Course(4, "Course 4", "Description 4", LocalDateTime.now().plusDays(4), "Marseille", 21.0, 200, "Cause 4")
        );

        when(courseService.findFiltered(null, null, null, "date")).thenReturn(courses);

        TemplateEngine mockEngine = mock(TemplateEngine.class);

        try (MockedStatic<ThymeleafConfig> mocked = mockStatic(ThymeleafConfig.class)) {
            mocked.when(ThymeleafConfig::getTemplateEngine).thenReturn(mockEngine);

            doAnswer(invocation -> {
                Context context = invocation.getArgument(1);
                PrintWriter writer = invocation.getArgument(2);
                List<Course> upcomingCourses = (List<Course>) context.getVariable("upcomingCourses");

                // Vérifier que la liste est bien limitée à 3 courses
                assertEquals(3, upcomingCourses.size());
                assertEquals("Course 1", upcomingCourses.get(0).getName());
                assertEquals("Course 3", upcomingCourses.get(2).getName());

                writer.write("template rendu");
                return null;
            }).when(mockEngine).process(eq("acceuil-2"), any(Context.class), any(PrintWriter.class));

            servlet.doGet(request, response);

            verify(response).setContentType("text/html;charset=UTF-8");
            verify(mockEngine).process(eq("acceuil-2"), any(Context.class), any(PrintWriter.class));
            assertTrue(responseWriter.toString().contains("template rendu"));
        }
    }

    @Test
    void whenExceptionThrown_shouldSendInternalServerError() throws Exception {
        when(courseService.findFiltered(null, null, null, "date"))
                .thenThrow(new RuntimeException("erreur inattendue"));

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), contains("erreur inattendue"));
    }
}
