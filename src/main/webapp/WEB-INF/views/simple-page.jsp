<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page | Color Run</title>
    <link rel="stylesheet" href="/runton-color/css/style-liste-course.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Navbar -->
<header class="navbar">
    <div class="logo">Color Run</div>
    <nav class="menu">
        <a href="/">Accueil</a>
        <a href="courses" class="active">Courses</a>
        <a href="#">Mes courses</a>
        <a href="#">Agenda</a>
        <a href="#">Devenir Bénévole</a>
        
        <!-- Affichage conditionnel selon l'état de connexion -->
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <!-- Utilisateur connecté -->
                <div style="display:flex; align-items:center; gap:12px;">
                    <span style="color:#6a82fb; font-weight:600;">Bonjour ${sessionScope.user.firstName}</span>
                    <!-- Déconnexion avec redirection vers la page actuelle -->
                    <a href="logout?redirect=${pageContext.request.requestURI}" style="color:#ff6a88; text-decoration:none; font-weight:600;">Se déconnecter</a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Utilisateur non connecté -->
                <a href="/" style="color:#6a82fb; text-decoration:none;">Se connecter</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>

<!-- Main Content -->
<section class="search-section">
    <section class="search-hero">
        <h1 class="main-title">Page d'exemple</h1>
        <p style="text-align:center;">Cette page montre comment gérer la déconnexion avec redirection.</p>
        
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <div style="background:#e8f5e8; color:#2e7d32; padding:20px; border-radius:12px; margin:24px auto; max-width:500px; text-align:center;">
                    <p>✅ Vous êtes connecté en tant que <strong>${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong></p>
                    <p>Rôle: <strong>${sessionScope.user.role}</strong></p>
                    <!-- Lien de déconnexion avec redirection vers l'accueil -->
                    <a href="logout" class="search-button" style="margin-top:16px;">Se déconnecter (retour accueil)</a>
                </div>
            </c:when>
            <c:otherwise>
                <div style="background:#fff3cd; color:#856404; padding:20px; border-radius:12px; margin:24px auto; max-width:500px; text-align:center;">
                    <p>⚠️ Vous n'êtes pas connecté</p>
                    <a href="/" class="search-button" style="margin-top:16px;">Aller à l'accueil pour se connecter</a>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</section>

<!-- Footer -->
<footer class="footer">
    <div class="footer-content">
        <div class="credits">
            <p>2025 © Color Run. Tous droits réservés.</p>
        </div>
    </div>
</footer>

</body>
</html> 