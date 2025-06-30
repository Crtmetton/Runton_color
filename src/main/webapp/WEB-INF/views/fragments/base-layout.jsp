<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}" default="Color Run"/></title>
    
    <!-- CSS de base -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    
    <!-- CSS additionnels selon la page -->
    <c:if test="${not empty additionalCSS}">
        <c:forEach var="css" items="${additionalCSS}">
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/${css}">
        </c:forEach>
    </c:if>
</head>
<body>
    <!-- Cercles de fond décoratifs -->
    <div class="bg-circle1"></div>
    <div class="bg-circle2"></div>
    <div class="bg-circle3"></div>
    
    <!-- Inclusion de la navbar -->
    <%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>
    
    <!-- Contenu principal de la page -->
    <main>
        <!-- Le contenu spécifique sera inséré ici -->
    </main>
    
    <!-- Inclusion du footer -->
    <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>
</body>
</html> 