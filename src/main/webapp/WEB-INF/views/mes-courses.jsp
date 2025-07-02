<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Courses | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-mes-courses.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Navbar unifiée -->
<%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>

<!-- Zone blanche autour du titre et du filtre -->
<main class="courses-container">
    <div class="mes-courses-bloc-blanc">
        <!-- Messages flash -->
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <i class="icon-check"></i> ${success}
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <i class="icon-warning"></i> ${error}
            </div>
        </c:if>

        <!-- En-tête -->
        <div style="text-align:center; margin-bottom:32px;">
            <h1 style="font-size:2.5rem; margin-bottom:16px;">Mes courses</h1>
            <div style="display:flex; justify-content:center; align-items:center; gap:32px; margin-bottom:24px; flex-wrap:wrap;">
                <div style="text-align:center;">
                    <div style="font-size:2rem; font-weight:bold; color:#6a82fb;">${userCourses.size()}</div>
                    <div style="color:#666;">Course(s) inscrite(s)</div>
                </div>
                
                <!-- Bouton pour les organisateurs -->
                <c:if test="${user != null && (user.role == 'ORGANIZER' || user.role == 'ADMIN')}">
                    <div style="text-align:center;">
                        <a href="${pageContext.request.contextPath}/my-created-courses" 
                           class="search-button" 
                           style="display:inline-block; text-decoration:none; padding:12px 20px; font-size:1rem; background:linear-gradient(135deg, #28a745 0%, #20c997 100%); white-space:nowrap;">
                            📋 Mes courses créées
                        </a>
                        <div style="color:#666; font-size:0.85rem; margin-top:4px;">Gérer vos événements</div>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Filtres supprimés -->

        <!-- Liste des courses -->
        <c:choose>
            <c:when test="${not empty userCourses}">
                <div id="mes-courses-list">
                    <c:forEach var="course" items="${userCourses}">
                        <article class="course-card" style="display:flex; flex-direction:row; align-items:stretch; margin-bottom:24px;">
                            <div class="course-image" style="min-width:260px; width:260px; height:200px; background:#e2e7ff; background-size:cover; background-position:center; border-radius:15px 0 0 15px;"></div>
                            <section class="course-details" style="flex:1; padding:24px;">
                                <h3 class="course-title" style="margin-bottom:8px;"><strong><em>${course.name}</em></strong></h3>
                                <p class="course-location" style="color:#6a82fb; margin-bottom:8px; font-weight:600;">${course.city}</p>
                                <p style="color:#666; margin-bottom:12px;">${course.description}</p>
                                
                                <div style="display:flex; gap:24px; margin-bottom:16px;">
                                    <div>
                                        <strong>Date:</strong> ${course.date.dayOfMonth}/${course.date.monthValue}/${course.date.year}
                                    </div>
                                    <div>
                                        <strong>Distance:</strong> ${course.distance} km
                                    </div>
                                    <div>
                                        <strong>Participants:</strong> ${course.currentParticipants}/${course.maxParticipants}
                                    </div>
                                </div>
                                
                                <section class="course-distances" style="margin-top:1rem;">
                                    <span class="distance-badge" style="background:#e2e7ff; color:#6a82fb; padding:8px 16px; border-radius:20px;">${course.distance} km</span>
                                </section>
                            </section>
                            <div style="display:flex; flex-direction:column; align-items:center; justify-content:center; gap:12px; padding:2rem;">
                                <a href="${pageContext.request.contextPath}/courseDetail?id=${course.id}" class="search-button" style="min-width:120px; text-decoration:none; display:inline-block; text-align:center;">
                                    Voir détails
                                </a>
                                <button class="search-button secondary" style="min-width:120px;" onclick="sendDossard('${course.id}')">📄 Dossard PDF</button>

                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Aucune course -->
                <div style="text-align:center; padding:64px 32px;">
                    <div style="font-size:4rem; color:#ddd; margin-bottom:24px;">🏃‍♂️</div>
                    <h3 style="color:#666; margin-bottom:16px;">Aucune course trouvée</h3>
                    <p style="color:#999; margin-bottom:32px;">
                        Vous n'êtes inscrit à aucune course pour le moment.<br>
                        Découvrez nos prochaines courses et inscrivez-vous !
                    </p>
                    <a href="${pageContext.request.contextPath}/courses" class="search-button" style="padding:12px 32px; font-size:1.1rem; text-decoration:none;">
                        Découvrir les courses
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<!-- Footer -->
<footer class="footer">
    <div class="footer-content">
        <div class="newsletter">
            <p>Abonnez-vous à notre newsletter</p>
            <form>
                <input type="email" placeholder="Votre email">
                <button type="submit">S'abonner</button>
            </form>
        </div>
        <div class="social-icons">
            <a href="#">📸 Instagram</a>
            <a href="#">📘 Facebook</a>
        </div>
        <div class="credits">
            <p>2025 © Color Run. Tous droits réservés.</p>
        </div>
    </div>
</footer>

<script>
    // Gestion de la déconnexion (si nécessaire)
    function logout() {
        if (confirm('Êtes-vous sûr de vouloir vous déconnecter ?')) {
            window.location.href = '${pageContext.request.contextPath}/logout';
        }
    }
    
    // Fonction pour télécharger le dossard PDF avec QR code intégré
    function sendDossard(courseId) {
        if (confirm('Voulez-vous télécharger votre dossard PDF avec QR code ?')) {
            // Afficher un message de chargement
            showLoadingMessage('Génération du dossard PDF en cours...');
            
            // Créer un lien invisible pour forcer le téléchargement
            const downloadLink = document.createElement('a');
            downloadLink.href = '${pageContext.request.contextPath}/dossard?courseId=' + courseId;
            downloadLink.download = 'dossard.pdf';
            downloadLink.style.display = 'none';
            document.body.appendChild(downloadLink);
            downloadLink.click();
            document.body.removeChild(downloadLink);
        }
    }
    
    // Fonction pour télécharger directement le QR code
    function downloadDossard(courseId) {
        if (confirm('Voulez-vous télécharger votre QR code de participation ?')) {
            // Afficher un message de chargement
            showLoadingMessage('Génération du QR code en cours...');
            
            // Ouvrir le téléchargement dans une nouvelle fenêtre
            window.open('${pageContext.request.contextPath}/dossard?courseId=' + courseId + '&action=download', '_blank');
        }
    }
    
    // Fonction pour afficher un message de chargement
    function showLoadingMessage(message) {
        // Créer une div de notification temporaire
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: #6a82fb;
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            z-index: 9999;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            font-weight: 500;
        `;
        notification.textContent = message;
        document.body.appendChild(notification);
        
        // Supprimer après 3 secondes
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 3000);
    }
    
    // Auto-masquer les messages flash après 5 secondes
    document.addEventListener('DOMContentLoaded', function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.opacity = '0';
                alert.style.transition = 'opacity 0.5s ease';
                setTimeout(() => {
                    alert.style.display = 'none';
                }, 500);
            }, 5000);
        });
    });
</script>
</body>
</html> 