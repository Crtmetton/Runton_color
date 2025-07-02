<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Créer une course | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-creation-course.css">
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Inclure la navbar dynamique -->
<jsp:include page="fragments/navbar.jsp"/>

<main class="main-container" style="display:flex; justify-content:center; align-items:center; min-height:80vh;">
    <section class="course-card" style="max-width:1200px; width:100%; margin:40px auto; padding:40px 48px; pointer-events:auto;">
        <h2 style="text-align:center; font-size:1.6rem; font-weight:700; margin-bottom:32px;">Créer une course</h2>
        
        <!-- Affichage des messages d'erreur -->
        <c:if test="${not empty error}">
            <div style="background:#ffe6e6; color:#d63031; padding:12px; border-radius:8px; margin-bottom:20px; border:1px solid #ff7675;">
                ${error}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/creation-course" method="post">
            <div style="display:flex; gap:24px; flex-wrap:wrap; margin-bottom:18px; align-items:flex-start;">
                <!-- Colonne gauche -->
                <div style="flex:1 1 0; min-width:220px; display:flex; flex-direction:column; gap:18px;">
                    <label for="nom" class="form-label">Nom de la course :</label>
                    <input id="nom" name="nom" type="text" class="filter-input form-field" placeholder="Ex : Color Run Paris" required>

                    <label for="lieu" class="form-label">Lieu complet :</label>
                    <input id="lieu" name="lieu" type="text" class="filter-input form-field" placeholder="Ex : Parc de la Villette, 211 Av. Jean Jaurès, 75019 Paris" required>

                    <label for="date" class="form-label">Date :</label>
                    <input id="date" name="date" type="date" class="filter-input form-field" required>
                    
                    <label for="heure" class="form-label">Heure :</label>
                    <input id="heure" name="heure" type="time" class="filter-input form-field" 
                           value="09:00" required>

                    <label for="distance" class="form-label">Distance (km) :</label>
                    <input id="distance" name="distance" type="number" step="0.1" class="filter-input form-field" placeholder="Ex : 5.0" value="5.0" required>
                </div>
                <!-- Colonne droite -->
                <div style="flex:1 1 0; min-width:220px; display:flex; flex-direction:column; gap:18px;">
                    <label for="maxParticipants" class="form-label">Nombre max de participants :</label>
                    <input id="maxParticipants" name="maxParticipants" type="number" class="filter-input form-field" min="1" placeholder="Ex : 200" value="100" required>

                    <label for="prix" class="form-label">Prix (€) :</label>
                    <input id="prix" name="prix" type="number" class="filter-input form-field" min="0" placeholder="Ex : 15" value="0" required>

                    <label for="cause" class="form-label">Cause :</label>
                    <input id="cause" name="cause" type="text" class="filter-input form-field" placeholder="Ex : Soutien à une association caritative">
                    
                    <!-- Champ vide pour équilibrer les colonnes -->
                    <div></div>
                    <div></div>
                </div>
            </div>
            
            <!-- Champ Description pleine largeur -->
            <div style="margin-top:24px;">
                <label for="description" class="form-label">Description :</label>
                <textarea id="description" name="description" class="filter-input form-field" style="width:100%; min-height:120px; margin-bottom:28px; resize:vertical;" placeholder="Décrivez votre course en détail : parcours, animations, objectifs, matériel fourni..." required></textarea>
            </div>
            
            <div style="display:flex; justify-content:center; margin-top:32px;">
                <button type="submit" class="search-button" style="width:180px; font-size:1.1rem; padding:12px 0;">Créer la course</button>
            </div>
        </form>
    </section>
</main>

<!-- Inclusion du footer -->
<jsp:include page="fragments/footer.jsp"/>

<style>
    /* Correction pour permettre l'interaction avec les champs */
    .course-card input, 
    .course-card textarea, 
    .course-card button {
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
    
    /* Style spécifique pour le champ time */
    input[type="time"].form-field {
        cursor: pointer;
    }
    
    /* Amélioration pour le textarea */
    textarea.form-field {
        resize: vertical;
        font-family: inherit;
        line-height: 1.5;
    }
    
    /* Responsive pour les colonnes */
    @media (max-width: 768px) {
        .main-container div[style*="display:flex"] {
            flex-direction: column !important;
        }
    }
</style>


</body>
</html> 