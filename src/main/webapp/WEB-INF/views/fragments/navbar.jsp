<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Navbar -->
<header class="navbar">
    <a href="${pageContext.request.contextPath}/" class="logo" style="text-decoration:none; color:inherit;">RUNTON</a>
    <nav class="menu">
        <!-- Navigation principale -->
        <a href="${pageContext.request.contextPath}/" 
           class="${pageContext.request.requestURI.endsWith('/') || pageContext.request.requestURI.contains('acceuil') ? 'active' : ''}">Accueil</a>
        
        <a href="${pageContext.request.contextPath}/courses" 
           class="${pageContext.request.requestURI.contains('/courses') ? 'active' : ''}">Courses</a>
        
        <!-- Mes courses uniquement pour utilisateurs connectés -->
        <c:if test="${isAuthenticated}">
            <a href="${pageContext.request.contextPath}/MyCourses" 
               class="${pageContext.request.requestURI.contains('/MyCourses') ? 'active' : ''}">Mes courses</a>
        </c:if>
        
        <!-- Affichage conditionnel selon le rôle -->
        <c:choose>
            <c:when test="${isAuthenticated}">
                <!-- Si USER ou PARTICIPANT : Devenir Organisateur -->
                <c:if test="${userRole == 'USER' || userRole == 'PARTICIPANT'}">
                    <a href="${pageContext.request.contextPath}/organizer/request" 
                       class="${pageContext.request.requestURI.contains('/organizer/request') ? 'active' : ''}">Devenir organisateur</a>
                </c:if>
                
                <!-- Si ORGANISATEUR : Créer course -->
                <c:if test="${isOrganizer}">
                    <a href="${pageContext.request.contextPath}/creation-course" 
                       class="${pageContext.request.requestURI.contains('/creation-course') ? 'active' : ''}">Créer course</a>
                </c:if>
                
                <!-- Si ADMIN : Page Admin -->
                <c:if test="${isAdmin}">
                    <a href="${pageContext.request.contextPath}/admin" 
                       class="${pageContext.request.requestURI.contains('/admin') ? 'active' : ''}">Admin</a>
                </c:if>
                
                <!-- Username à droite avec popup de déconnexion -->
                <div class="user-section" style="margin-left: auto;">
                    <div class="user-dropdown" onclick="toggleUserMenu()">
                        <span class="username" style="font-weight:700; font-style:italic; font-size:1.05rem; color:inherit;">${userName}</span>
                        <svg width="12" height="8" viewBox="0 0 12 8" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M1 1.5L6 6.5L11 1.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </div>
                    
                    <!-- Dropdown menu -->
                    <div id="user-menu" class="user-menu-dropdown" style="display: none;">
                        <a href="${pageContext.request.contextPath}/profile" class="logout-link">
                            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M8 1a3 3 0 110 6 3 3 0 010-6zM2 15c0-2.7614 2.2386-5 5-5h2c2.7614 0 5 2.2386 5 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Mon profil
                        </a>
                        <a href="${pageContext.request.contextPath}/logout" class="logout-link">
                            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M6 2H4C3.46957 2 2.96086 2.21071 2.58579 2.58579C2.21071 2.96086 2 3.46957 2 4V12C2 12.5304 2.21071 13.0391 2.58579 13.4142C2.96086 13.7893 3.46957 14 4 14H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M11 11L14 8L11 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M14 8H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                            Déconnexion
                        </a>
                    </div>
                </div>
            </c:when>
            
            <c:otherwise>
                <!-- Utilisateur non connecté -->
                <a href="#" class="account-icon" title="Mon compte" style="margin-left:auto;" onclick="openAccountPopup()">
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
        <div style="font-size:1.8rem; font-weight:800; color:#000; margin-bottom:32px; margin-top:8px; font-family:inherit;">RUNTON</div>
        
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
</c:if>

<script>
    // Fonction pour le menu utilisateur connecté
    function toggleUserMenu() {
        const menu = document.getElementById('user-menu');
        if (menu.style.display === 'none' || menu.style.display === '') {
            menu.style.display = 'block';
        } else {
            menu.style.display = 'none';
        }
    }

    // Fermer le menu quand on clique ailleurs
    document.addEventListener('click', function(event) {
        const userSection = document.querySelector('.user-section');
        const menu = document.getElementById('user-menu');
        
        if (userSection && menu && !userSection.contains(event.target)) {
            menu.style.display = 'none';
        }
    });

    // Fonctions pour le popup de connexion (utilisateurs non connectés)
    function openAccountPopup() {
        const overlay = document.getElementById('account-popup-overlay');
        if (overlay) {
            overlay.style.display = 'block';
            document.getElementById('account-popup-content').style.display = 'block';
            document.getElementById('register-popup-content').style.display = 'none';
        }
    }
    
    function closeAccountPopup() {
        const overlay = document.getElementById('account-popup-overlay');
        if (overlay) {
            overlay.style.display = 'none';
        }
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