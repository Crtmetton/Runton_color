<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Courses | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/fr.js"></script>
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Navbar unifiée -->
<%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>

<!-- Hero Section -->
<section class="search-section">
    <section class="search-hero">
        <h1 class="main-title" style="font-size: 2.2rem;">Découvrez nos prochaines courses</h1>
        
        <!-- Barre de recherche -->
        <div class="search-container">
            <div class="search-input-container">
                <input type="text" class="search-input" placeholder="Rechercher une course..." id="searchInput">
            </div>
            <div class="filter-input-container">
                <input type="text" class="filter-input" placeholder="Ville ou région..." id="cityFilter">
            </div>
            <div class="filter-input-container date-container">
                <input type="text" class="filter-input" placeholder="Date (JJ/MM/AAAA)" id="dateFilter" data-value="">
            </div>
            <button class="search-button" onclick="filterCourses()">Rechercher</button>
        </div>
    </section>
</section>

<main class="main-container">
    <!-- Messages d'information -->
    <c:if test="${not empty success}">
        <div class="alert alert-success" style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 15px; margin-bottom: 20px;">
            ${success}
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error" style="background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 15px; margin-bottom: 20px;">
            ${error}
        </div>
    </c:if>

    <!-- Zone blanche autour du titre et du nombre de courses -->
    <div class="acceuil-bloc-blanc" style="margin-bottom: 32px;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
            <div>
                <h2 style="font-size:1.8rem; font-weight:700; color:#333; margin-bottom:8px;">Courses disponibles</h2>
                <p style="color:#666; margin:0;">${courses.size()} course(s) trouvée(s)</p>
            </div>
        </div>
    </div>

    <!-- Grid des courses -->
    <div class="courses-container">
        <c:choose>
            <c:when test="${not empty courses}">
                <div class="courses-grid" id="coursesGrid">
                    <c:forEach var="course" items="${courses}">
                        <article class="course-card" data-course-name="${course.name}" data-course-city="${course.city}" data-course-date="${course.date.year}-${course.date.monthValue < 10 ? '0' : ''}${course.date.monthValue}-${course.date.dayOfMonth < 10 ? '0' : ''}${course.date.dayOfMonth}">
                            <!-- Image de la course -->
                            <div class="course-image" style="background-image: url('${pageContext.request.contextPath}/images/course-placeholder.jpg');"></div>
                            
                            <!-- Détails de la course -->
                            <div class="course-details">
                                <h3 class="course-title">${course.name}</h3>
                                <div class="course-location">${course.city}</div>
                                <div style="color:#666; margin-bottom:20px; font-size:0.95rem; line-height:1.5;">
                                    ${course.description}
                                </div>
                                
                                <!-- Informations de la course -->
                                <div style="display:flex; gap:16px; margin-bottom:20px; flex-wrap:wrap;">
                                    <div class="distance-badge">${course.distance} km</div>
                                    <div style="color:#666; font-size:0.9rem;">
                                        📅 ${course.date.dayOfMonth}/${course.date.monthValue}/${course.date.year}
                                    </div>
                                    <div style="color:#666; font-size:0.9rem;">
                                        👥 ${course.maxParticipants} places max
                                    </div>
                                </div>
                                
                                <!-- Prix et boutons d'action -->
                                <div style="display:flex; justify-content:space-between; align-items:center;">
                                    <c:choose>
                                        <c:when test="${course.prix > 0}">
                                            <div class="course-price" style="margin-right:10px;">${course.prix}€</div>
                                        </c:when>
                                        <c:otherwise>
                                    <div class="course-price" style="margin-right:10px;">Gratuit</div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div style="display:flex; gap:8px;">
                                        <c:choose>
                                            <c:when test="${isAuthenticated}">
                                                <!-- Vérifier si l'utilisateur est déjà inscrit -->
                                                <c:choose>
                                                    <c:when test="${userParticipations[course.id]}">
                                                        <!-- Utilisateur déjà inscrit -->
                                                        <span class="search-button" style="min-width:100px; font-size:0.95rem; background:#e0e0e0; color:#666; cursor:not-allowed;">
                                                            Déjà inscrit
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <!-- Bouton Participer -->
                                                        <a href="${pageContext.request.contextPath}/participate?courseId=${course.id}" 
                                                           class="search-button" style="min-width:100px; font-size:0.95rem;">
                                                            Participer
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                                <!-- Bouton Détails -->
                                                <a href="${pageContext.request.contextPath}/courseDetail?id=${course.id}" 
                                                   class="search-button secondary" style="min-width:80px; font-size:0.95rem;">
                                                    Détail
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- Utilisateur non connecté -->
                                                <button class="search-button secondary" onclick="openAccountPopup()" 
                                                        style="min-width:90px; font-size:0.85rem;">
                                                    Se connecter
                                                </button>
                                                <a href="${pageContext.request.contextPath}/courseDetail?id=${course.id}" 
                                                   class="search-button secondary" style="min-width:80px; font-size:0.95rem;">
                                                    Détail
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Aucune course trouvée -->
                <div class="acceuil-bloc-blanc" style="text-align:center; padding:60px 40px;">
                    <div style="font-size:4rem; margin-bottom:20px;">🏃‍♂️</div>
                    <h3 style="color:#666; margin-bottom:16px;">Aucune course disponible</h3>
                    <p style="color:#999; margin-bottom:32px;">
                        Il n'y a aucune course programmée pour le moment.<br>
                        Revenez bientôt pour découvrir nos prochaines aventures colorées !
                    </p>
                    <c:if test="${isOrganizer}">
                        <a href="${pageContext.request.contextPath}/creation-course" class="search-button">
                            Créer la première course
                        </a>
                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<script>
    // Fonction de filtrage des courses
    function filterCourses() {
        const searchTerm = document.getElementById('searchInput').value.toLowerCase();
        const cityFilter = document.getElementById('cityFilter').value.toLowerCase();
        const dateFilter = document.getElementById('dateFilter').dataset.value || '';
        
        const courses = document.querySelectorAll('.course-card');
        let visibleCount = 0;
        
        courses.forEach(course => {
            const courseName = course.getAttribute('data-course-name').toLowerCase();
            const courseCity = course.getAttribute('data-course-city').toLowerCase();
            const courseDate = course.getAttribute('data-course-date');
            
            let isVisible = true;
            
            // Filtre par nom
            if (searchTerm && !courseName.includes(searchTerm)) {
                isVisible = false;
            }
            
            // Filtre par ville
            if (cityFilter && !courseCity.includes(cityFilter)) {
                isVisible = false;
            }
            
            // Filtre par date (comparaison exacte au format YYYY-MM-DD)
            if (dateFilter && courseDate && courseDate !== dateFilter) {
                isVisible = false;
            }
            
            if (isVisible) {
                course.style.display = 'block';
                visibleCount++;
            } else {
                course.style.display = 'none';
            }
        });
        
        // Mettre à jour le compteur dans l'interface
        const counter = document.querySelector('.acceuil-bloc-blanc p');
        if (counter) {
            counter.textContent = `${visibleCount} course(s) trouvée(s)`;
        }
        console.log(`${visibleCount} course(s) trouvée(s)`);
    }
    
    // Filtrage en temps réel
    document.addEventListener('DOMContentLoaded', function() {
        const searchInput = document.getElementById('searchInput');
        const cityFilter = document.getElementById('cityFilter');
        const dateInput = document.getElementById('dateFilter');
        
        if (searchInput) searchInput.addEventListener('input', filterCourses);
        if (cityFilter) cityFilter.addEventListener('input', filterCourses);
        if (dateInput) dateInput.addEventListener('change', filterCourses);
        
        console.log('Page des courses chargée avec ${courses.size()} courses');
    });

    // Initialize Flatpickr for French locale
    flatpickr("#dateFilter", {
        locale: "fr",
        dateFormat: "d/m/Y",
        allowInput: true,
        onChange: function(selectedDates, dateStr, instance) {
            if (selectedDates.length) {
                const iso = selectedDates[0].toISOString().split('T')[0];
                instance._input.dataset.value = iso; // store ISO for filtering
            } else {
                instance._input.dataset.value = '';
            }
            filterCourses();
        }
    });
</script>

<!-- Inclusion du footer -->
<jsp:include page="../fragments/footer.jsp"/>

</body>
</html> 