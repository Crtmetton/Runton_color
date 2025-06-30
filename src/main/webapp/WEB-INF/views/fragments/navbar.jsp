<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Navbar -->
<header class="navbar">
    <div class="logo">Color Run</div>
    <nav class="menu">
        <a href="${pageContext.request.contextPath}/" 
           class="${pageContext.request.requestURI.endsWith('/') || pageContext.request.requestURI.contains('acceuil') ? 'active' : ''}">Accueil</a>
        
        <a href="${pageContext.request.contextPath}/courses" 
           class="${pageContext.request.requestURI.contains('/courses') ? 'active' : ''}">Courses</a>
        
        <a href="${pageContext.request.contextPath}/MyCourses" 
           class="${pageContext.request.requestURI.contains('/MyCourses') ? 'active' : ''}">Mes courses</a>
        
        
        <a href="${pageContext.request.contextPath}/organizer/request" 
           class="${pageContext.request.requestURI.contains('/organizer/request') ? 'active' : ''}">Devenir Organisateur</a>
        
        <!-- Affichage conditionnel selon l'état de connexion avec TOKEN -->
        <c:choose>
            <c:when test="${isAuthenticated}">
                <!-- Utilisateur connecté via TOKEN -->
                <div style="display:flex; align-items:center; gap:12px; margin-left:24px;">
                    <span style="color:#6a82fb; font-weight:600;">Bonjour ${userName}</span>
                    <span style="background:#28a745; color:white; padding:2px 6px; border-radius:8px; font-size:0.8rem; font-weight:600;">
                        ${userRole}
                    </span>
                    
                    <!-- Menu selon le rôle -->
                    <c:if test="${isAdmin}">
                        <a href="${pageContext.request.contextPath}/admin" 
                           style="color:#dc3545; text-decoration:none; font-weight:600; margin-left:8px;">Admin</a>
                    </c:if>
                    
                    <c:if test="${isOrganizer}">
                        <a href="${pageContext.request.contextPath}/creation-course" 
                           style="color:#007bff; text-decoration:none; font-weight:600; margin-left:8px;">Créer</a>
                        <a href="${pageContext.request.contextPath}/MyCourses" 
                           style="color:#007bff; text-decoration:none; font-weight:600;">Mes courses</a>
                    </c:if>
                    
                    <a href="${pageContext.request.contextPath}/logout" 
                       style="color:#ff6a88; text-decoration:none; font-weight:600; margin-left:8px;">Déconnexion</a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Utilisateur non connecté -->
                <a href="#" class="account-icon" title="Mon compte" style="margin-left:24px;" onclick="openAccountPopup()">
                    <svg width="28" height="28" viewBox="0 0 28 28" fill="none" style="display:inline-block;vertical-align:middle;" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="14" cy="14" r="13" stroke="#6a82fb" stroke-width="2" fill="#fff"/>
                        <circle cx="14" cy="11" r="5" fill="#e2e7ff" stroke="#6a82fb" stroke-width="1.5"/>
                        <path d="M6 22c1.5-4 14.5-4 16 0" stroke="#6a82fb" stroke-width="1.5" fill="none" stroke-linecap="round"/>
                    </svg>
                </a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>

<!-- Popup compte utilisateur (connexion/inscription) - UNIQUEMENT pour les utilisateurs non connectés -->
<c:if test="${not isAuthenticated}">
<div id="account-popup-overlay" style="display:none; position:fixed; inset:0; background:rgba(0,0,0,0.38); z-index:9999;">
    <div id="account-popup" style="position:fixed; top:0; right:0; width:370px; max-width:100vw; height:100vh; background:#fff; box-shadow:-2px 0 24px rgba(0,0,0,0.10); z-index:10000; display:flex; flex-direction:column; align-items:center; padding:32px 28px 0 28px;">
        <button onclick="closeAccountPopup()" style="position:absolute; top:18px; right:24px; background:none; border:none; font-size:1.5rem; color:#ff6a88; cursor:pointer;">×</button>
        <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo Color Run" style="height:38px; margin-bottom:32px; margin-top:8px;">
        
        <!-- Contenu de connexion -->
        <div id="account-popup-content">
            <h2 style="font-size:1.15rem; margin-bottom:18px; text-align:left;">Se connecter</h2>
            <form action="${pageContext.request.contextPath}/login" method="POST">
                <label style="font-size:0.98rem; font-weight:600;">Adresse email :</label>
                <input type="email" name="email" required style="width:100%; margin-bottom:16px; margin-top:4px; padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <label style="font-size:0.98rem; font-weight:600;">Mot de passe :</label>
                <input type="password" name="password" required style="width:100%; margin-bottom:24px; margin-top:4px; padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <button type="submit" class="search-button" style="width:100%; margin-bottom:18px;">Se connecter</button>
            </form>
            <div style="text-align:center; font-size:0.95rem;">
                Tu n'as pas de compte ? <a href="#" id="show-register" style="color:#6a82fb; text-decoration:underline; cursor:pointer;">Créer le tien</a>
            </div>
        </div>
        
        <!-- Contenu d'inscription -->
        <div id="register-popup-content" style="display:none; width:100%;">
            <h2 style="font-size:1.15rem; margin-bottom:18px; text-align:left;">Créer un compte</h2>
            <form action="${pageContext.request.contextPath}/register" method="POST">
                <label style="font-size:0.98rem; font-weight:600;">Nom :</label>
                <input type="text" name="lastName" required style="width:100%; margin-bottom:12px; margin-top:4px; padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <label style="font-size:0.98rem; font-weight:600;">Prénom :</label>
                <input type="text" name="firstName" required style="width:100%; margin-bottom:12px; margin-top:4px; padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <label style="font-size:0.98rem; font-weight:600;">Adresse email :</label>
                <input type="email" name="email" required style="width:100%; margin-bottom:12px; margin-top:4px; padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <label style="font-size:0.98rem; font-weight:600;">Mot de passe :</label>
                <input type="password" name="password" required style="width:100%; margin-bottom:20px; margin-top:4px; padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <button type="submit" class="search-button" style="width:100%; margin-bottom:18px;">S'inscrire</button>
            </form>
            <div style="text-align:center; font-size:0.95rem;">
                Tu as déjà un compte ? <a href="#" id="show-login" style="color:#6a82fb; text-decoration:underline; cursor:pointer;">Connecte-toi</a>
            </div>
        </div>
    </div>
</div>

<script>
    function openAccountPopup() {
        document.getElementById('account-popup-overlay').style.display = 'block';
        document.getElementById('account-popup-content').style.display = 'block';
        document.getElementById('register-popup-content').style.display = 'none';
    }
    
    function closeAccountPopup() {
        document.getElementById('account-popup-overlay').style.display = 'none';
    }
    
    // Initialisation des événements
    document.addEventListener('DOMContentLoaded', function() {
        const showRegister = document.getElementById('show-register');
        const showLogin = document.getElementById('show-login');
        const overlay = document.getElementById('account-popup-overlay');
        
        if (showRegister) {
            showRegister.onclick = function(e) {
                e.preventDefault();
                document.getElementById('account-popup-content').style.display = 'none';
                document.getElementById('register-popup-content').style.display = 'block';
            };
        }
        
        if (showLogin) {
            showLogin.onclick = function(e) {
                e.preventDefault();
                document.getElementById('register-popup-content').style.display = 'none';
                document.getElementById('account-popup-content').style.display = 'block';
            };
        }
        
        if (overlay) {
            overlay.onclick = function(e) {
                if (e.target === this) closeAccountPopup();
            };
        }
    });
</script>
</c:if> 