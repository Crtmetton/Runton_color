<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil | Color Run</title>
    <link rel="stylesheet" href="/runton-color/css/style-liste-course.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Inclusion de la navbar -->
<%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>

<!-- Hero Section -->
<section class="search-section">
    <section class="search-hero">
        <h1 class="main-title">Vivez l'expérience Color Run près de chez vous !</h1>
        <p style="text-align:center;">Inscrivez-vous à une course colorée, découvrez les prochaines étapes et rejoignez la communauté.</p>
        <div style="display:flex; justify-content:center; gap:16px; margin-top:24px;">
            <button class="search-button" onclick="window.location.href='${pageContext.request.contextPath}/courses'">Voir les courses</button>
        </div>
    </section>
</section>

<div class="acceuil-bloc-blanc">
    <!-- Ajoute un padding horizontal pour écarter tous les éléments du bord -->
    <div style="padding-left:48px; padding-right:48px;">
        <!-- Concept Section -->
        <section class="courses-container">
            <div style="display:flex;align-items:center;gap:40px;justify-content:center;">
                <div style="width:160px;height:160px;background:#e2e7ff;border-radius:20px;"></div>
                <div style="max-width:500px; margin-left:48px; margin-right:48px;">
                    <h2>Découvrez le concept de la Color Run</h2>
                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                        Participez à une course festive et colorée, accessible à tous, sans chrono ni pression, où le plaisir et la convivialité priment !
                    </p>
                </div>
            </div>
        </section>

        <!-- Pourquoi participer -->
        <section class="courses-container">
            <h2 style="text-align:center;margin-bottom:32px;">Pourquoi participer ?</h2>
            <div style="display:flex;justify-content:center;gap:60px;">
                <div style="text-align:center;">
                    <div style="width:80px;height:80px;background:#e2e7ff;border-radius:15px;margin:0 auto 20px;"></div>
                    <div style="margin:0 24px;">Titre à remplir</div>
                </div>
                <div style="text-align:center;">
                    <div style="width:80px;height:80px;background:#e2e7ff;border-radius:15px;margin:0 auto 20px;"></div>
                    <div style="margin:0 24px;">Titre à remplir</div>
                </div>
                <div style="text-align:center;">
                    <div style="width:80px;height:80px;background:#e2e7ff;border-radius:15px;margin:0 auto 20px;"></div>
                    <div style="margin:0 24px;">Titre à remplir</div>
                </div>
            </div>
        </section>

        <!-- Prochaines courses -->
        <section class="courses-container">
            <h2 style="text-align:center;margin-bottom:32px;">Prochaines courses à ne pas manquer</h2>
            
            <c:choose>
                <c:when test="${not empty upcomingCourses}">
                    <c:forEach var="course" items="${upcomingCourses}" begin="0" end="1" varStatus="status">
                        <c:choose>
                            <c:when test="${status.index % 2 == 0}">
                                <!-- Course 1 : Texte à gauche, image à droite -->
                                <div style="display:flex;gap:60px;align-items:center;margin-bottom:40px;justify-content:center;">
                                    <div style="flex:1;max-width:400px;">
                                        <h3 style="margin-bottom:16px;font-size:1.4rem;">${course.name}</h3>
                                        <p style="color:#666;margin-bottom:24px;line-height:1.5;">
                                            <c:choose>
                                                <c:when test="${not empty course.description}">
                                                    ${course.description}
                                                </c:when>
                                                <c:otherwise>
                                                    Une course colorée et festive qui vous attend ! Rejoignez-nous pour vivre une expérience unique.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <div style="display:flex; gap:16px;">
                                            <button class="search-button" onclick="window.location.href='${pageContext.request.contextPath}/course/detail?id=${course.id}'">Découvrir</button>
                                            <button class="search-button secondary" onclick="window.location.href='${pageContext.request.contextPath}/courses'">Voir toutes</button>
                                        </div>
                                    </div>
                                    <div style="width:320px;height:180px;background:linear-gradient(135deg, #667eea 0%, #764ba2 100%);border-radius:20px;display:flex;align-items:center;justify-content:center;color:white;font-size:32px;font-weight:bold;">
                                        🏃‍♀️
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Course 2 : Image à gauche, texte à droite -->
                                <div style="display:flex;gap:60px;align-items:center;margin-bottom:40px;justify-content:center;">
                                    <div style="width:320px;height:180px;background:linear-gradient(135deg, #f093fb 0%, #f5576c 100%);border-radius:20px;display:flex;align-items:center;justify-content:center;color:white;font-size:32px;font-weight:bold;">
                                        🏃‍♀️
                                    </div>
                                    <div style="flex:1;max-width:400px;">
                                        <h3 style="margin-bottom:16px;font-size:1.4rem;">${course.name}</h3>
                                        <p style="color:#666;margin-bottom:24px;line-height:1.5;">
                                            <c:choose>
                                                <c:when test="${not empty course.description}">
                                                    ${course.description}
                                                </c:when>
                                                <c:otherwise>
                                                    Une course colorée et festive qui vous attend ! Rejoignez-nous pour vivre une expérience unique.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <div style="display:flex; gap:16px;">
                                            <button class="search-button" onclick="window.location.href='${pageContext.request.contextPath}/course/detail?id=${course.id}'">Découvrir</button>
                                            <button class="search-button secondary" onclick="window.location.href='${pageContext.request.contextPath}/courses'">Voir toutes</button>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <!-- Aucune course disponible -->
                    <div style="text-align:center;padding:40px;">
                        <p style="color:#666;font-size:18px;">Aucune course disponible pour le moment.</p>
                        <p style="color:#999;">Revenez bientôt pour découvrir nos prochaines Color Run !</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</div>
<!-- Fin du bloc blanc -->

<!-- Footer -->
<!-- Inclusion du footer -->
<%@ include file="/WEB-INF/views/fragments/footer.jsp" %>


</body>
</html>
