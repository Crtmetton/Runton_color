<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes courses créées | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-mes-courses.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Inclure la navbar dynamique -->
<jsp:include page="fragments/navbar.jsp"/>

<main class="main-container">
    <!-- Hero Section -->
    <section class="search-section">
        <div class="search-hero">
            <h1 class="main-title">Mes courses créées</h1>
        </div>
    </section>

    <!-- Messages d'information -->
    <c:if test="${not empty success}">
        <div style="background:#e6f7e6; color:#28a745; padding:12px; border-radius:8px; margin:20px auto; max-width:1200px; border:1px solid #28a745;">
            ${success}
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div style="background:#ffe6e6; color:#d63031; padding:12px; border-radius:8px; margin:20px auto; max-width:1200px; border:1px solid #ff7675;">
            ${error}
        </div>
    </c:if>

    <!-- Section des courses créées -->
    <section class="courses-grid" style="display:block; max-width:1200px; margin:0 auto;">
        
        <c:choose>
            <c:when test="${empty createdCourses}">
                <!-- Aucune course créée -->
                <div class="course-card" style="text-align:center; padding:60px 40px; background:#f8f9ff; border:2px dashed #e2e7ff;">
                    <h3 style="color:#6a82fb; margin-bottom:16px; font-size:1.4rem;">Aucune course créée</h3>
                    <p style="color:#666; margin-bottom:30px; line-height:1.6;">
                        Vous n'avez pas encore créé de course.<br>
                        Commencez par organiser votre première Color Run !
                    </p>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Affichage des courses créées -->
                <div class="courses-grid" style="display:block; max-width:1200px; margin:0 auto;">
                    <c:forEach var="course" items="${createdCourses}">
                        <div class="course-card" style="display:flex; flex-direction:row; align-items:stretch; position:relative; overflow:hidden; min-height:200px; margin-bottom:32px; border-radius:16px;">
                            <!-- Image placeholder -->
                            <div style="min-width:200px; width:200px; background:linear-gradient(135deg, #6a82fb 0%, #fc5c7d 100%); position:relative; display:flex; align-items:center; justify-content:center;">
                                <div style="color:white; font-size:3rem; font-weight:bold;">CR</div>
                            </div>
                            
                            <!-- Contenu de la carte -->
                            <div style="flex:1; padding:24px; display:flex; flex-direction:column;">
                                <h3 style="font-size:1.4rem; font-weight:700; margin-bottom:12px; color:#333; line-height:1.3;">
                                    ${course.name}
                                </h3>
                                
                                <div style="color:#666; font-size:1rem; margin-bottom:16px; display:flex; align-items:center; gap:8px;">
                                    <span style="font-weight:500;">${course.city}</span>
                                </div>
                                
                                <div style="color:#ff6a88; font-weight:600; margin-bottom:20px; font-size:1.1rem;">
                                    ${course.date.dayOfMonth}/${course.date.monthValue}/${course.date.year} 
                                    à ${course.date.hour}:${course.date.minute < 10 ? '0' : ''}${course.date.minute}
                                </div>
                                
                                <!-- Statistiques de la course -->
                                <div style="display:flex; gap:40px; margin-bottom:20px; font-size:0.95rem; color:#666;">
                                    <span><strong>Distance:</strong> ${course.distance} km</span>
                                    <span><strong>Participants max:</strong> ${course.maxParticipants}</span>
                                    <span><strong>Prix:</strong> 
                                        <c:choose>
                                            <c:when test="${course.prix > 0}">
                                                <strong class="course-price">${course.prix}€</strong>
                                            </c:when>
                                            <c:otherwise>
                                                <strong class="course-price">Gratuit</strong>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                
                                <!-- Description -->
                                <p style="color:#666; font-size:0.95rem; margin-bottom:auto; line-height:1.5;">
                                    ${course.description}
                                </p>
                            </div>
                            
                            <!-- Boutons d'action -->
                            <div style="display:flex; flex-direction:column; justify-content:center; gap:16px; padding:24px; min-width:200px;">
                                <a href="${pageContext.request.contextPath}/courseDetail?id=${course.id}" 
                                   class="search-button" 
                                   style="text-decoration:none; padding:12px 16px; font-size:0.95rem; text-align:center; width:100%; box-sizing:border-box; background-color:#ff6a88; color:white;">
                                    Détail
                                </a>
                                <a href="${pageContext.request.contextPath}/course/edit?id=${course.id}" 
                                   class="search-button" 
                                   style="text-decoration:none; padding:12px 16px; font-size:0.95rem; text-align:center; background:linear-gradient(135deg, #28a745 0%, #20c997 100%); width:100%; box-sizing:border-box;">
                                    Modifier
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<!-- Inclusion du footer -->
<jsp:include page="fragments/footer.jsp"/>

<style>
    /* Styles spécifiques pour la page des courses créées */
    .course-card {
        border: none;
        box-shadow: none;
        border-radius: 16px;
        background: white;
        overflow: hidden;
        display: flex;
        flex-direction: column;
    }
    
    .course-card:hover {
        transform: none;
        box-shadow: none;
        border-color: transparent;
    }
    
    /* Animation au survol des boutons */
    .search-button:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 15px rgba(106, 130, 251, 0.3);
    }
    
    /* Responsive */
    @media (max-width: 768px) {
        .courses-grid {
            grid-template-columns: 1fr !important;
            padding: 16px !important;
        }
        
        .main-container {
            padding: 0 16px;
        }
        
        .course-card {
            margin-bottom: 20px;
        }
        
        /* Ajustement des boutons sur mobile */
        .course-card .search-button {
            font-size: 0.85rem !important;
            padding: 8px !important;
        }
    }
    
    /* Animation d'apparition */
    .course-card {
        animation: fadeInUp 0.6s ease forwards;
    }
    
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    /* Délai d'animation pour chaque carte */
    .course-card:nth-child(1) { animation-delay: 0.1s; }
    .course-card:nth-child(2) { animation-delay: 0.2s; }
    .course-card:nth-child(3) { animation-delay: 0.3s; }
    .course-card:nth-child(4) { animation-delay: 0.4s; }
    .course-card:nth-child(5) { animation-delay: 0.5s; }
    .course-card:nth-child(6) { animation-delay: 0.6s; }
</style>

<script>
    // Formatage automatique de l'heure
    document.addEventListener('DOMContentLoaded', function() {
        console.log('Page "Mes courses créées" chargée');
        
        // Optionnel : Actualisation automatique du nombre de participants
        // Peut être implémenté plus tard via AJAX
    });
</script>

</body>
</html> 