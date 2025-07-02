<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier la course | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-creation-course.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-acceuil.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Inclure la navbar dynamique -->
<jsp:include page="fragments/navbar.jsp"/>

<main class="main-container" style="display:flex; justify-content:center; align-items:center; min-height:80vh;">
    <section class="course-card" style="max-width:1200px; width:100%; margin:40px auto; padding:40px 48px; pointer-events:auto;">
        <h2 style="text-align:center; font-size:1.6rem; font-weight:700; margin-bottom:32px;">
            ✏️ Modifier la course "${course.name}"
        </h2>
        
        <!-- Affichage des messages d'erreur -->
        <c:if test="${not empty error}">
            <div style="background:#ffe6e6; color:#d63031; padding:12px; border-radius:8px; margin-bottom:20px; border:1px solid #ff7675;">
                ${error}
            </div>
        </c:if>
        
        <!-- Affichage des messages de succès -->
        <c:if test="${not empty success}">
            <div style="background:#e6f7e6; color:#28a745; padding:12px; border-radius:8px; margin-bottom:20px; border:1px solid #28a745;">
                ${success}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/course/edit" method="post">
            <input type="hidden" name="courseId" value="${course.id}">
            
            <div style="display:flex; gap:24px; flex-wrap:wrap; margin-bottom:18px; align-items:flex-start;">
                <!-- Colonne gauche -->
                <div style="flex:1 1 0; min-width:220px; display:flex; flex-direction:column; gap:18px;">
                    <label for="nom" class="form-label">Nom de la course :</label>
                    <input id="nom" name="nom" type="text" class="filter-input form-field" 
                           placeholder="Ex : Color Run Paris" 
                           value="${course.name}" required>

                    <label for="lieu" class="form-label">Lieu complet :</label>
                    <input id="lieu" name="lieu" type="text" class="filter-input form-field" 
                           placeholder="Ex : Parc de la Villette, 211 Av. Jean Jaurès, 75019 Paris" 
                           value="${course.city}" required>

                    <label for="date" class="form-label">Date :</label>
                    <input id="date" name="date" type="date" class="filter-input form-field" 
                           value="${course.date.toLocalDate()}" required>
                    
                    <label for="heure" class="form-label">Heure :</label>
                    <input id="heure" name="heure" type="time" class="filter-input form-field" 
                           value="${course.date.toLocalTime()}" required>

                    <label for="distance" class="form-label">Distance (km) :</label>
                    <input id="distance" name="distance" type="number" step="0.1" class="filter-input form-field" 
                           placeholder="Ex : 5.0" value="${course.distance}" required>
                </div>
                <!-- Colonne droite -->
                <div style="flex:1 1 0; min-width:220px; display:flex; flex-direction:column; gap:18px;">
                    <label for="maxParticipants" class="form-label">Nombre max de participants :</label>
                    <input id="maxParticipants" name="maxParticipants" type="number" class="filter-input form-field" 
                           min="1" placeholder="Ex : 200" value="${course.maxParticipants}" required>

                    <label for="prix" class="form-label">Prix (€) :</label>
                    <input id="prix" name="prix" type="number" class="filter-input form-field" 
                           min="0" placeholder="Ex : 15" value="${course.prix}" required>

                    <label for="cause" class="form-label">Cause :</label>
                    <input id="cause" name="cause" type="text" class="filter-input form-field" 
                           placeholder="Ex : Soutien à une association caritative" value="${course.cause}">
                    
                    <!-- Champ vide pour équilibrer les colonnes -->
                    <div></div>
                    <div></div>
                </div>
            </div>
            
            <!-- Champ Description pleine largeur -->
            <div style="margin-top:24px;">
                <label for="description" class="form-label">Description :</label>
                <textarea id="description" name="description" class="filter-input form-field" 
                          style="width:100%; min-height:120px; margin-bottom:28px; resize:vertical;" 
                          placeholder="Décrivez votre course en détail : parcours, animations, objectifs, matériel fourni..." 
                          required>${course.description}</textarea>
            </div>
            
            <div style="display:flex; justify-content:center; gap:20px; margin-top:32px;">
                <a href="${pageContext.request.contextPath}/course/detail?id=${course.id}" 
                   class="search-button secondary" 
                   style="width:140px; font-size:1.1rem; padding:12px 0; text-decoration:none; text-align:center; background:#f8f9fa; color:#666; border:2px solid #ddd;">
                    Annuler
                </a>
                <button type="submit" class="search-button" 
                        style="width:180px; font-size:1.1rem; padding:12px 0; background: linear-gradient(135deg, #28a745 0%, #20c997 100%);">
                    ✅ Sauvegarder les modifications
                </button>
            </div>
        </form>
    </section>
</main>

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

<style>
    /* Correction pour permettre l'interaction avec les champs */
    .course-card input, 
    .course-card textarea, 
    .course-card button,
    .course-card a {
        pointer-events: auto !important;
    }
    
    /* Champs plus jolis et focus amélioré */
    .form-label {
        font-weight: 600;
        font-size: 1rem;
        margin-bottom: 6px;
        color: #6a82fb;
        letter-spacing: 0.2px;
        display: block;
    }
    .form-field {
        border: 2px solid #e2e7ff !important;
        border-radius: 8px !important;
        padding: 12px !important;
        font-size: 1rem;
        transition: all 0.3s ease;
        background: #fff !important;
        width: 100%;
        box-sizing: border-box;
    }
    .form-field:focus {
        outline: none !important;
        border-color: #6a82fb !important;
        box-shadow: 0 0 0 3px rgba(106, 130, 251, 0.1) !important;
        background: #fafbff !important;
    }
    
    /* Amélioration pour le textarea */
    textarea.form-field {
        resize: vertical;
        font-family: inherit;
        line-height: 1.5;
    }
    
    /* Style spécial pour les champs modifiés */
    .form-field:not([value=""]) {
        background: #f8fffa !important;
        border-color: #28a745 !important;
    }
    
    /* Style pour le bouton d'annulation */
    .search-button.secondary:hover {
        background: #e9ecef !important;
        border-color: #adb5bd !important;
    }
    
    /* Responsive pour les colonnes */
    @media (max-width: 768px) {
        .main-container div[style*="display:flex"] {
            flex-direction: column !important;
        }
        
        .main-container div[style*="gap:20px"] {
            flex-direction: column !important;
            gap: 12px !important;
        }
        
        .main-container div[style*="gap:20px"] .search-button,
        .main-container div[style*="gap:20px"] a {
            width: 100% !important;
        }
    }
</style>

<script>
    // Script pour formater automatiquement les dates lors du chargement
    document.addEventListener('DOMContentLoaded', function() {
        // Les dates sont déjà formatées correctement via les attributs value des inputs
        console.log('Formulaire de modification de course chargé');
    });
</script>
</body>
</html> 