<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Devenir organisateur | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-devenir-organisateur.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>
<!-- Inclusion de la navbar -->
<%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>

<main class="main-container" style="display:flex; justify-content:center; align-items:center; min-height:80vh;">
    <section class="course-card">
        <h2 class="main-title">Devenir organisateur</h2>
        
        <!-- Messages d'erreur et de succès -->
        <% if (request.getAttribute("error") != null) { %>
            <div style="background-color: #ff6b6b; color: white; padding: 12px; border-radius: 8px; margin-bottom: 16px; text-align: center;">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div style="background-color: #51cf66; color: white; padding: 12px; border-radius: 8px; margin-bottom: 16px; text-align: center;">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("hasRequest") == null || !(Boolean)request.getAttribute("hasRequest")) { %>
            <form action="${pageContext.request.contextPath}/organizer/request" method="POST">
                <label for="motivation" class="form-label">Vos informations :</label>
                <textarea id="motivation" name="motivation" class="filter-input form-field" placeholder="Présentez-vous, votre projet, vos motivations..." required></textarea>
                <div style="display:flex; justify-content:center; margin-top:32px;">
                    <button type="submit" class="search-button" style="width:220px; font-size:1.1rem; padding:12px 0;">Envoyer</button>
                </div>
            </form>
        <% } else { %>
            <div style="text-align: center; padding: 32px; background-color: #e7f5ff; border-radius: 12px; border: 1px solid #74c0fc;">
                <h3 style="color: #1971c2; margin-bottom: 16px;">Demande déjà soumise</h3>
                <p style="color: #495057;">Votre demande pour devenir organisateur a déjà été soumise et est en cours de traitement.</p>
            </div>
        <% } %>
    </section>
</main>

<!-- Inclusion du footer -->
<%@ include file="/WEB-INF/views/fragments/footer.jsp" %>


</body>
</html> 