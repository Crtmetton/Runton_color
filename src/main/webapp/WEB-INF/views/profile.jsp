<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon profil | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <style>
        /* Style du formulaire de profil */
        .profile-form label {
            font-size: 0.95rem;
            font-weight: 600;
            color: #333;
        }

        .profile-form input {
            width: 100%;
            padding: 12px 16px;
            border-radius: 12px;
            border: 2px solid #e2e7ff;
            background: #f8f9ff;
            font-size: 1rem;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
            box-sizing: border-box;
        }

        .profile-form input:focus {
            outline: none;
            border-color: #6a82fb;
            box-shadow: 0 0 0 3px rgba(106, 130, 251, 0.25);
        }

        .profile-form button.search-button {
            align-self: flex-start;
            padding: 12px 32px;
            font-size: 1rem;
        }
    </style>
</head>
<body>
<jsp:include page="fragments/navbar.jsp"/>

<main class="main-container" style="max-width:600px; margin:40px auto;">
    <h1 style="text-align:center; margin-bottom:32px;">Mon profil</h1>

    <c:if test="${not empty error}">
        <div style="background:#ffe6e6; color:#d63031; padding:12px; border-radius:8px; margin-bottom:20px;">
            ${error}
        </div>
    </c:if>
    <c:if test="${not empty success}">
        <div style="background:#e6f7e6; color:#28a745; padding:12px; border-radius:8px; margin-bottom:20px;">
            ${success}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/profile" class="profile-form" style="display:flex; flex-direction:column; gap:18px;">
        <label>Prénom :</label>
        <input type="text" name="firstName" value="${user.firstName}" required>

        <label>Nom :</label>
        <input type="text" name="lastName" value="${user.lastName}" required>

        <label>Mot de passe actuel :</label>
        <input type="password" name="currentPassword">

        <label>Nouveau mot de passe (laisser vide pour ne pas changer) :</label>
        <input type="password" name="password">

        <label>Confirmer le mot de passe :</label>
        <input type="password" name="confirmPassword">

        <button type="submit" class="search-button">Enregistrer</button>
    </form>
</main>

<jsp:include page="fragments/footer.jsp"/>
</body>
</html> 