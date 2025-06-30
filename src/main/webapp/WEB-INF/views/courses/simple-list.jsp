<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Courses - Runton Color</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
</head>
<body>
    <!-- Utilisation de la navbar centralisée avec système de token -->
    <jsp:include page="../fragments/navbar.jsp" />

    <div class="container mt-4">
        <h1>🏃‍♂️ Courses Color Run Disponibles</h1>
        
        <!-- Informations utilisateur -->
        <c:if test="${isAuthenticated}">
            <div class="alert alert-info">
                <strong>👋 ${userName}</strong> - Vous consultez en tant que <strong>${userRole}</strong>
            </div>
        </c:if>
        
        <!-- Affichage des courses -->
        <div class="row">
            <div class="col-12">
                <p>Nombre de courses : <strong>${courses.size()}</strong></p>
                
                <c:choose>
                    <c:when test="${not empty courses}">
                        <div class="row">
                            <c:forEach var="course" items="${courses}">
                                <div class="col-md-6 col-lg-4 mb-4">
                                    <div class="card h-100">
                                        <div class="card-body">
                                            <h5 class="card-title">${course.name}</h5>
                                            <p class="card-text">${course.description}</p>
                                            <p class="text-muted">
                                                <i class="bi bi-geo-alt"></i> ${course.city}<br>
                                                <i class="bi bi-speedometer2"></i> ${course.distance} km<br>
                                                <i class="bi bi-calendar"></i> ${course.date}<br>
                                                <i class="bi bi-people"></i> ${course.maxParticipants} participants max
                                            </p>
                                            
                                            <!-- Actions selon le rôle -->
                                            <div class="mt-3">
                                                <c:if test="${isAuthenticated}">
                                                    <!-- Bouton participer pour tous les utilisateurs connectés -->
                                                    <a href="${pageContext.request.contextPath}/participate?courseId=${course.id}" 
                                                       class="btn btn-primary btn-sm">
                                                        <i class="bi bi-person-plus"></i> Participer
                                                    </a>
                                                    
                                                    <!-- Bouton détails pour voir plus d'infos -->
                                                    <a href="${pageContext.request.contextPath}/courseDetail?id=${course.id}" 
                                                       class="btn btn-outline-info btn-sm">
                                                        <i class="bi bi-info-circle"></i> Détails
                                                    </a>
                                                </c:if>
                                                
                                                <c:if test="${not isAuthenticated}">
                                                    <!-- Message pour utilisateurs non connectés -->
                                                    <small class="text-muted">
                                                        <i class="bi bi-lock"></i> Connectez-vous pour participer
                                                    </small>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning">
                            <i class="bi bi-exclamation-triangle"></i> 
                            Aucune course disponible pour le moment.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Script pour interactions utilisateur
        document.addEventListener('DOMContentLoaded', function() {
            console.log('Page des courses chargée avec ${courses.size()} courses');
            
            // Log des informations utilisateur pour debug
            <c:if test="${isAuthenticated}">
                console.log('Utilisateur connecté: ${userName} (${userRole})');
            </c:if>
            <c:if test="${not isAuthenticated}">
                console.log('Utilisateur non connecté');
            </c:if>
        });
    </script>
</body>
</html> 