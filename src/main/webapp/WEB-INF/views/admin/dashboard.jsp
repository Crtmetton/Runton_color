<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administration | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-mes-courses.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Navigation -->
<%@ include file="../fragments/navbar.jsp" %>

<section class="search-section">
    <section class="search-hero">
        <h1 class="main-title">Administration</h1>
        <p style="text-align:center; color:#6a82fb; margin-top:16px; font-size:1.1rem;">
            Gestion des utilisateurs et des demandes d'organisateur
        </p>
    </section>
</section>

<main class="courses-container">
    <div class="mes-courses-bloc-blanc">
        
        <!-- Messages de succès/erreur -->
        <c:if test="${param.success != null}">
            <div style="background:#d4edda; color:#155724; padding:12px; border-radius:8px; margin-bottom:24px; border:1px solid #c3e6cb;">
                ${param.success}
            </div>
        </c:if>
        <c:if test="${param.error != null}">
            <div style="background:#f8d7da; color:#721c24; padding:12px; border-radius:8px; margin-bottom:24px; border:1px solid #f5c6cb;">
                ${param.error}
            </div>
        </c:if>
        
        <!-- Statistiques -->
        <section style="margin-bottom:48px;">
            <h2 style="font-size:1.8rem; margin-bottom:24px; text-align:center; color:#333;">Statistiques</h2>
            <div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(200px, 1fr)); gap:20px; margin-bottom:32px;">
                <div style="background:#f8f9ff; border:2px solid #e2e7ff; border-radius:12px; padding:20px; text-align:center;">
                    <div style="font-size:2.5rem; font-weight:bold; color:#6a82fb;">${totalUsers}</div>
                    <div style="color:#666; font-size:1rem;">Utilisateurs total</div>
                </div>
                <div style="background:#fff8f0; border:2px solid #ffe5cc; border-radius:12px; padding:20px; text-align:center;">
                    <div style="font-size:2.5rem; font-weight:bold; color:#ff8c42;">${pendingRequestsCount}</div>
                    <div style="color:#666; font-size:1rem;">Demandes en attente</div>
                </div>
                <div style="background:#f0fff8; border:2px solid #ccffe5; border-radius:12px; padding:20px; text-align:center;">
                    <div style="font-size:2.5rem; font-weight:bold; color:#42ff8c;">${organizersCount}</div>
                    <div style="color:#666; font-size:1rem;">Organisateurs actifs</div>
                </div>
            </div>
        </section>

        <!-- Demandes d'organisateur en attente -->
        <section style="margin-bottom:48px;">
            <h2 style="font-size:2rem; margin-bottom:32px; text-align:center;">Demandes d'organisateur en attente</h2>
            
            <c:choose>
                <c:when test="${empty pendingRequests}">
                    <div style="text-align:center; color:#666; font-style:italic; padding:40px; background:#f8f9ff; border-radius:12px; border:2px dashed #e2e7ff;">
                        Aucune demande en attente !<br>
                        <small>Toutes les demandes ont été traitées.</small>
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="pending-requests-list" class="courses-grid" style="grid-template-columns: 1fr;">
                        <c:forEach var="request" items="${pendingRequests}">
                            <article class="course-card" style="display:flex; flex-direction:row; align-items:stretch;">
                                <section class="course-details" style="flex:1;">
                                    <h3 class="course-title">
                                        <strong>${request.requester.firstName} ${request.requester.lastName}</strong>
                                        <span style="font-size:0.8rem; background:#6a82fb; color:white; padding:2px 8px; border-radius:12px; margin-left:8px;">
                                            ${request.requester.role}
                                        </span>
                                    </h3>
                                    <p class="course-location">${request.requester.email}</p>
                                    <p class="course-price" style="font-size:1.1rem; color:#6a82fb; margin-bottom:1rem;">
                                        <strong>Motivation :</strong>
                                    </p>
                                    <section class="course-distances" style="margin-top:0;">
                                        <span class="distance-badge" style="background:#e2e7ff; color:#333; font-weight:500; font-size:1rem; padding:0.7rem 1.2rem; white-space:normal; line-height:1.4;">
                                            ${request.reason}
                                        </span>
                                    </section>
                                    <p style="font-size:0.9rem; color:#999; margin-top:8px;">
                                        Demande soumise le : ${request.submissionDate.dayOfMonth}/${request.submissionDate.monthValue}/${request.submissionDate.year}
                                    </p>
                                </section>
                                <div style="display:flex; flex-direction:column; align-items:center; justify-content:center; gap:12px; padding:2rem;">
                                    <form method="post" action="${pageContext.request.contextPath}/admin" style="margin:0;">
                                        <input type="hidden" name="action" value="approve-organizer">
                                        <input type="hidden" name="requestId" value="${request.id}">
                                        <button type="submit" class="search-button" style="min-width:120px; background:#28a745; border-color:#28a745;">
                                            Accepter
                                        </button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin" style="margin:0;">
                                        <input type="hidden" name="action" value="reject-organizer">
                                        <input type="hidden" name="requestId" value="${request.id}">
                                        <button type="submit" class="search-button secondary" style="min-width:120px; background:#dc3545; border-color:#dc3545; color:white;">
                                            Refuser
                                        </button>
                                    </form>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
        
        <!-- Liste de tous les utilisateurs -->
        <section>
            <h2 style="font-size:2rem; margin-bottom:32px; text-align:center;">Gestion des utilisateurs</h2>
            
            <c:choose>
                <c:when test="${empty allUsers}">
                    <div style="text-align:center; color:#666; font-style:italic; padding:40px;">
                        Aucun utilisateur trouvé.
                    </div>
                </c:when>
                <c:otherwise>
                    <div id="users-list" class="courses-grid" style="grid-template-columns: 1fr;">
                        <c:forEach var="user" items="${allUsers}">
                            <article class="course-card" style="display:flex; flex-direction:row; align-items:stretch;">
                                <section class="course-details" style="flex:1;">
                                    <h3 class="course-title">
                                        <strong>${user.firstName} ${user.lastName}</strong>
                                        <c:choose>
                                            <c:when test="${user.role == 'ADMIN'}">
                                                <span style="font-size:0.8rem; background:#dc3545; color:white; padding:2px 8px; border-radius:12px; margin-left:8px;">${user.role}</span>
                                            </c:when>
                                            <c:when test="${user.role == 'ORGANISATEUR'}">
                                                <span style="font-size:0.8rem; background:#ff8c42; color:white; padding:2px 8px; border-radius:12px; margin-left:8px;">${user.role}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size:0.8rem; background:#6a82fb; color:white; padding:2px 8px; border-radius:12px; margin-left:8px;">${user.role}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${not user.enabled}">
                                            <span style="font-size:0.8rem; background:#666; color:white; padding:2px 8px; border-radius:12px; margin-left:4px;">
                                                DÉSACTIVÉ
                                            </span>
                                        </c:if>
                                    </h3>
                                    <p class="course-location">${user.email}</p>
                                </section>
                                <div style="display:flex; flex-direction:column; align-items:center; justify-content:center; gap:12px; padding:2rem;">
                                    <c:choose>
                                        <c:when test="${user.role == 'ADMIN'}">
                                            <button class="search-button" style="min-width:120px; background:#6c757d; border-color:#6c757d; cursor:not-allowed;" disabled>
                                                Administrateur
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <form method="post" action="${pageContext.request.contextPath}/admin" style="margin:0;" 
                                                  onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action est irréversible.');">
                                                <input type="hidden" name="action" value="delete-user">
                                                <input type="hidden" name="userId" value="${user.id}">
                                                <button type="submit" class="search-button secondary" style="min-width:120px; background:#dc3545; border-color:#dc3545; color:white;">
                                                    Supprimer
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<script>
    // Confirmation avant suppression
    function confirmDelete(userName) {
        return confirm("Êtes-vous sûr de vouloir supprimer l'utilisateur \"" + userName + "\" ?\n\nCette action est irréversible et supprimera :\n- Le compte utilisateur\n- Toutes ses participations\n- Tous ses messages\n- Toutes ses données associées");
    }
    
    // Amélioration de l'UX - Auto-refresh après action
    <c:if test="${param.success != null}">
        setTimeout(function() {
            // Masquer le message de succès après 5 secondes
            var successMsg = document.querySelector('[style*="background:#d4edda"]');
            if (successMsg) {
                successMsg.style.opacity = '0';
                successMsg.style.transition = 'opacity 0.5s ease';
                setTimeout(function() {
                    successMsg.style.display = 'none';
                }, 500);
            }
        }, 5000);
    </c:if>
</script>

<footer class="footer">
    <div class="footer-content">
        <div class="newsletter">
            <p>Administration Color Run</p>
            <p style="font-size:0.9rem; color:#999;">Gestion des utilisateurs et des demandes d'organisateur</p>
        </div>
        <div class="social-links">
            <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
            <a href="${pageContext.request.contextPath}/courses">Voir les courses</a>
        </div>
    </div>
</footer>

</body>
</html> 