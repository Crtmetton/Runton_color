<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Infos Course | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    
    <!-- Leaflet CSS pour la carte -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY="
          crossorigin=""/>
    
    <!-- Leaflet JavaScript pour la carte -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
            integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo="
            crossorigin=""></script>
    <style>
        .popup-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 10000;
            justify-content: center;
            align-items: center;
        }
        
        .participation-popup {
            background: white;
            border-radius: 30px;
            padding: 40px;
            max-width: 500px;
            width: 90%;
            text-align: center;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        }
        
        .popup-title {
            font-size: 1.5rem;
            font-weight: bold;
            color: #6a82fb;
            margin-bottom: 20px;
        }
        
        .popup-price {
            font-size: 2rem;
            font-weight: bold;
            color: #ff6a88;
            margin: 20px 0;
        }
        
        .popup-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }
        
        .btn-pay {
            background: linear-gradient(135deg, #6a82fb 0%, #fc5c7d 100%);
            color: white;
            border: none;
            padding: 15px 30px;
            border-radius: 25px;
            font-size: 1.1rem;
            font-weight: bold;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        .btn-pay:hover {
            transform: translateY(-2px);
        }
        
        .btn-cancel {
            background: #f8f9fa;
            color: #666;
            border: 2px solid #ddd;
            padding: 13px 30px;
            border-radius: 25px;
            font-size: 1.1rem;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .btn-cancel:hover {
            background: #e9ecef;
            border-color: #adb5bd;
        }
    </style>
</head>
<body>
<div class="bg-circle1"></div>
<div class="bg-circle2"></div>
<div class="bg-circle3"></div>

<!-- Navbar unifiée -->
<%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>

<!-- Hero Section pour la course -->
<section class="search-section">
    <section class="search-hero" style="display:flex; flex-direction:column; align-items:center; gap:32px;">
        <div style="display:flex; gap:32px; align-items:center; width:100%; justify-content:center;">
            <div style="width:220px; height:220px; background:#e2e7ff; border-radius:32px; background-size:cover; background-position:center;"></div>
            <div style="flex:1; min-width:220px;">
                <h1 class="main-title" style="margin-bottom:12px;">${course.name}</h1>
                <div style="font-size:1.2rem; color:#6a82fb; font-weight:600;">${course.city}</div>
                <div style="font-size:1.05rem; color:#ff6a88; font-weight:600; margin-top:8px;">
                    ${course.date.dayOfMonth}/${course.date.monthValue}/${course.date.year}
                </div>
            </div>
        </div>
    </section>
</section>

<main class="main-container" style="margin-top:32px;">
    <div style="display:flex; gap:32px; align-items:flex-start; flex-wrap:wrap;">
        <!-- Colonne gauche : infos principales -->
        <div style="flex:2; min-width:340px;">
            <!-- En-tête infos -->
            <section class="course-card" style="padding:32px 32px 24px 32px; margin-bottom:24px;">
                <div style="display:flex; gap:32px; align-items:flex-start;">
                    <div style="min-width:60px; text-align:center;">
                        <div style="font-size:2.2rem; font-weight:800; color:#ff6a88;">
                            ${course.date.dayOfMonth}
                        </div>
                        <div style="font-size:1rem; color:#666;" id="dayOfWeekFrench">
                            ${course.date.dayOfWeek}
                        </div>
                    </div>
                    <div style="flex:1;">
                        <div style="font-size:1.1rem; font-weight:700; color:#6a82fb;" id="fullDateFrench">
                            ${course.date.dayOfWeek} ${course.date.dayOfMonth} ${course.date.month} ${course.date.year}
                        </div>
                        <div style="margin-top:8px; font-size:1rem; font-weight:600;">${course.city}</div>
                        <div style="font-size:0.95rem; color:#6a82fb;">Distance: ${course.distance} km</div>
                    </div>
                </div>
                <!-- Prix principal et bouton paiement -->
                <div style="margin-top:28px; display:flex; align-items:center; gap:24px; flex-wrap:wrap;">
                    <c:choose>
                        <c:when test="${course.prix > 0}">
                            <span style="font-size:1.25rem; font-weight:700; color:#ff6a88;">${course.prix}€</span>
                        </c:when>
                        <c:otherwise>
                            <span style="font-size:1.25rem; font-weight:700; color:#ff6a88;">Gratuit</span>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${isAuthenticated && !isRegistered}">
                        <button class="search-button" style="min-width:160px; font-size:1.08rem;" onclick="openParticipationPopup()">
                            Participer
                        </button>
                    </c:if>
                    <c:if test="${isRegistered}">
                        <span style="color:#28a745; font-weight:bold;">✓ Déjà inscrit</span>
                    </c:if>
                    <c:if test="${!isAuthenticated}">
                        <button class="search-button" style="min-width:160px; font-size:1.08rem;" onclick="openAuthModal()">
                            Se connecter pour participer
                        </button>
                    </c:if>
                    
                    <!-- Bouton modifier visible seulement par le créateur -->
                    <c:if test="${isCreator}">
                        <a href="${pageContext.request.contextPath}/course/edit?id=${course.id}"
                           class="btn-modify"
                           style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
                                  color: white;
                                  border: none;
                                  padding: 12px 24px;
                                  border-radius: 25px;
                                  font-size: 1.08rem;
                                  font-weight: bold;
                                  text-decoration: none;
                                  cursor: pointer;
                                  transition: transform 0.2s, box-shadow 0.2s;
                                  min-width: 140px;
                                  text-align: center;
                                  display: inline-block;
                                  box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);"
                           onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 6px 20px rgba(40, 167, 69, 0.4)';"
                           onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 4px 15px rgba(40, 167, 69, 0.3)';">
                            ✏️ Modifier ma course
                        </a>
                    </c:if>
                </div>
            </section>
            
            <!-- Description -->
            <section class="course-card" style="padding:32px; margin-bottom:24px;">
                <div style="font-size:1.08rem; color:#222; margin-bottom:18px;">
                    ${course.description}
                </div>
                <div style="margin-bottom:14px;">
                    <b>Informations de la course :</b>
                    <ul style="margin:8px 0 0 18px; color:#444; font-size:0.98rem;">
                        <li>Distance : ${course.distance} km</li>
                        <li>Participants maximum : ${course.maxParticipants}</li>
                        <li>Places restantes : ${course.maxParticipants - participants.size()}</li>
                    </ul>
                </div>
            </section>
            
            <!-- Liste des inscrits -->
            <section class="course-card" style="padding:24px; margin-bottom:24px;">
                <div style="font-weight:700; font-size:1.1rem; margin-bottom:12px;">Liste des inscrits</div>
                <div style="color:#666; margin-bottom:8px;">${participants.size()} inscrit(s)</div>
                <button id="show-participants" class="search-button secondary" style="min-width:100px; font-size:0.95rem; padding:6px 18px;">Voir la liste →</button>
            </section>
        </div>
    </div>

    <!-- Popup participants -->
    <div id="participants-popup" style="display:none; position:fixed; top:0; left:0; width:100vw; height:100vh; background:rgba(0,0,0,0.18); z-index:9999; align-items:center; justify-content:center;">
        <div style="background:#fff; border-radius:32px; max-width:500px; width:90%; margin:auto; box-shadow:0 8px 32px rgba(0,0,0,0.13); padding:32px 24px; position:relative;">
            <button onclick="document.getElementById('participants-popup').style.display='none'" style="position:absolute; top:18px; right:24px; background:none; border:none; font-size:1.5rem; color:#ff6a88; cursor:pointer;">×</button>
            <h2 style="font-size:1.3rem; margin-bottom:18px; text-align:center;">Liste des participants</h2>
            <table style="width:100%; border-collapse:collapse; margin-bottom:0;">
                <thead>
                <tr style="background:#e2e7ff;">
                    <th style="padding:8px; border-radius:12px 0 0 12px;">Nom</th>
                    <th style="padding:8px;">Prénom</th>
                    <th style="padding:8px; border-radius:0 12px 12px 0;">Email</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="participant" items="${participants}">
                    <tr>
                        <td style="padding:8px;">${participant.user.lastName}</td>
                        <td style="padding:8px;">${participant.user.firstName}</td>
                        <td style="padding:8px;">${participant.user.email}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Tchat et carte sous la colonne principale -->
    <div style="display:flex; gap:32px; align-items:flex-start; flex-wrap:wrap; margin-top:32px;">
        <div style="flex:2; min-width:340px;">
            <!-- Chat Simple de la course -->
            <section class="course-card" style="padding:32px 24px; margin-bottom:24px;">
                <div style="font-weight:700; margin-bottom:8px;">Chat de la course</div>
                
                <!-- Zone de messages -->
                <div id="chat-messages" style="background:#f8f9ff; border-radius:16px; border:1px solid #e2e7ff; padding:16px; min-height:200px; max-height:400px; overflow-y:auto; margin-bottom:12px;">
                    <c:choose>
                        <c:when test="${empty discussions}">
                            <div style="text-align:center; color:#666; font-style:italic; padding:20px;">
                                Aucun message pour l'instant<br>
                                <small>Soyez le premier à démarrer la conversation !</small>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="discussion" items="${discussions}">
                                <div class="message-item" data-message-id="${discussion.id}" style="margin-bottom:12px; border-bottom:1px solid #eee; padding-bottom:8px;">
                                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
                                        <strong style="color:#6a82fb; font-size:0.9rem;">
                                            <c:choose>
                                                <c:when test="${not empty discussion.expediteur}">
                                                    ${discussion.expediteur.firstName} ${discussion.expediteur.lastName}
                                                </c:when>
                                                <c:otherwise>
                                                    Utilisateur inconnu
                                                </c:otherwise>
                                            </c:choose>
                                        </strong>
                                        <div style="display:flex; align-items:center; gap:8px;">
                                            <span style="font-size:0.75rem; color:#999;">
                                                ${discussion.date.dayOfMonth}/${discussion.date.monthValue}/${discussion.date.year} ${discussion.date.hour}:${discussion.date.minute}
                                            </span>
                                            <!-- Bouton supprimer visible seulement par l'organisateur -->
                                            <c:if test="${isCreator}">
                                                <button onclick="deleteMessage('${discussion.id}')" 
                                                        style="background:none; border:none; color:#ff6a88; cursor:pointer; font-size:0.8rem; padding:2px 4px; border-radius:4px;"
                                                        title="Supprimer ce message">
                                                    X
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>
                                    <!-- Contenu du message -->
                                    <div style="color:#333; font-size:0.95rem; line-height:1.4;">
                                        ${discussion.contenu}
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <!-- Zone de saisie -->
                <c:if test="${isAuthenticated}">
                    <form id="chatForm" style="margin-top:8px; display:flex; gap:8px;" onsubmit="sendMessage(event)">
                        <input type="text" 
                               id="messageInput" 
                               placeholder="Tapez votre message..." 
                               style="flex:1; border-radius:12px; border:1px solid #bbb; padding:10px 16px; font-size:1rem;"
                               maxlength="500">
                        <button type="submit" 
                                style="background:#6a82fb; color:white; border:none; border-radius:8px; padding:10px 16px; cursor:pointer;">
                            Envoyer
                        </button>
                        <input type="hidden" id="courseId" value="${course.id}">
                    </form>
                </c:if>
                
                <c:if test="${!isAuthenticated}">
                    <div style="text-align:center; color:#666; font-style:italic; padding:16px; background:#f8f9fa; border-radius:12px;">
                        Connectez-vous pour participer à la discussion
                    </div>
                </c:if>
            </section>
            
            <!-- Carte GPS Interactive -->
            <section class="course-card" style="padding:24px;">
                <div style="font-weight:700; margin-bottom:8px;">📍 Lieu de la course</div>
                <div style="color:#666; margin-bottom:12px; font-size:0.95rem;">
                    ${course.city}
                </div>
                <div id="map" style="width:100%; height:400px; border-radius:16px; overflow:hidden; border:1px solid #e2e7ff; background:#f8f9fa;">
                    <div style="display:flex; align-items:center; justify-content:center; height:100%; color:#666;">
                        <div style="text-align:center;">
                            🗺️ Chargement de la carte...<br>
                            <small>Localisation en cours</small>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</main>

<!-- Popup de participation -->
<c:if test="${showParticipationPopup}">
<div class="popup-overlay" id="participationPopup" style="display: flex;">
    <div class="participation-popup">
        <div class="popup-title">Inscription à "${course.name}"</div>
        <div style="color:#666; margin-bottom:20px;">
            ${course.city} - ${course.date.dayOfMonth}/${course.date.monthValue}/${course.date.year}
        </div>
        <c:choose>
            <c:when test="${course.prix > 0}">
                <div class="popup-price">${course.prix}€</div>
            </c:when>
            <c:otherwise>
                <div class="popup-price">Gratuit</div>
            </c:otherwise>
        </c:choose>
        <div style="color:#666; margin-bottom:20px;">
            Une course accessible à tous !<br>
            <strong style="color:#28a745;">📱 Votre QR code de participation vous sera envoyé par email après inscription</strong>
        </div>
        <div class="popup-buttons">
            <form method="post" action="${pageContext.request.contextPath}/participate" style="display:inline;">
                <input type="hidden" name="courseId" value="${course.id}">
                <button type="submit" class="btn-pay">
                    Confirmer l'inscription
                </button>
            </form>
            <button class="btn-cancel" onclick="closeParticipationPopup()">
                Annuler
            </button>
        </div>
    </div>
</div>
</c:if>

<!-- Footer -->
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

<!-- Variables pour éviter les erreurs JavaScript -->
<script type="text/javascript">
    var courseInfo = {
        name: '<c:out value="${course.name}" escapeXml="true"/>',
        distance: '${course.distance}',
        day: '${course.date.dayOfMonth}',
        month: '${course.date.monthValue}',
        year: '${course.date.year}',
        dayOfWeek: '${course.date.dayOfWeek}',
        description: '<c:out value="${course.description}" escapeXml="true"/>',
        maxParticipants: '${course.maxParticipants}',
        currentParticipants: '${participants.size()}',
        city: '<c:out value="${course.city}" escapeXml="true"/>'
    };
</script>

<script>
    // ===== TRADUCTION DES JOURS EN FRANÇAIS =====
    function translateDayToFrench(englishDay) {
        const dayTranslations = {
            'MONDAY': 'Lundi',
            'TUESDAY': 'Mardi', 
            'WEDNESDAY': 'Mercredi',
            'THURSDAY': 'Jeudi',
            'FRIDAY': 'Vendredi',
            'SATURDAY': 'Samedi',
            'SUNDAY': 'Dimanche'
        };
        return dayTranslations[englishDay.toUpperCase()] || englishDay;
    }
    
    function translateMonthToFrench(englishMonth) {
        const monthTranslations = {
            'JANUARY': 'Janvier',
            'FEBRUARY': 'Février',
            'MARCH': 'Mars',
            'APRIL': 'Avril',
            'MAY': 'Mai',
            'JUNE': 'Juin',
            'JULY': 'Juillet',
            'AUGUST': 'Août',
            'SEPTEMBER': 'Septembre',
            'OCTOBER': 'Octobre',
            'NOVEMBER': 'Novembre',
            'DECEMBER': 'Décembre'
        };
        return monthTranslations[englishMonth.toUpperCase()] || englishMonth;
    }

    // ===== FONCTIONNALITÉS CHAT =====
    
    function sendMessage(event) {
        event.preventDefault();
        
        const messageInput = document.getElementById('messageInput');
        const courseIdElement = document.getElementById('courseId');
        
        console.log('messageInput:', messageInput);
        console.log('courseIdElement:', courseIdElement);
        
        if (!messageInput || !courseIdElement) {
            console.error('Éléments manquants:', { messageInput, courseIdElement });
            alert('Erreur : éléments du formulaire non trouvés');
            return false;
        }
        
        const message = messageInput.value.trim();
        const courseId = courseIdElement.value;
        
        console.log('Message à envoyer:', message);
        console.log('Course ID:', courseId);
        
        if (!message) {
            alert('Veuillez saisir un message');
            messageInput.focus();
            return false;
        }
        
        if (!courseId) {
            alert('Erreur : ID de course manquant');
            return false;
        }
        
        // Désactiver temporairement le champ pendant l'envoi
        messageInput.disabled = true;
        
        // Préparer les données du formulaire
        const formData = new URLSearchParams();
        formData.append('content', message);
        formData.append('courseId', courseId);
        
        console.log('Données à envoyer:', formData.toString());
        
        // Envoyer la requête POST
        fetch('${pageContext.request.contextPath}/courseDetail', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        })
        .then(response => {
            console.log('Réponse du serveur:', response.status);
            if (response.ok) {
                messageInput.value = '';
                // Recharger la page pour voir le nouveau message
                window.location.reload();
            } else {
                throw new Error('Erreur lors de envoi du message: ' + response.status);
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            alert('Erreur lors de envoi du message. Veuillez réessayer.');
        })
        .finally(() => {
            messageInput.disabled = false;
            if (messageInput) {
                messageInput.focus();
            }
        });
        
        return false;
    }
    
    // Supprimer un message
    function deleteMessage(messageId) {
        if (!confirm('Êtes-vous sûr de vouloir supprimer ce message ?')) {
            return;
        }
        
        fetch('${pageContext.request.contextPath}/discussion/delete?id=' + messageId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                throw new Error('Erreur lors de la suppression');
            }
        })
        .catch(error => {
            console.error('Erreur:', error);
            alert('Erreur lors de la suppression du message. Veuillez réessayer.');
        });
    }
    
    // Auto-focus et scroll
    document.addEventListener('DOMContentLoaded', function() {
        // Traduire les jours en français
        const dayOfWeekElement = document.getElementById('dayOfWeekFrench');
        const fullDateElement = document.getElementById('fullDateFrench');
        
        if (dayOfWeekElement) {
            const englishDay = courseInfo.dayOfWeek;
            dayOfWeekElement.textContent = translateDayToFrench(englishDay);
        }
        
        if (fullDateElement) {
            const englishDay = courseInfo.dayOfWeek;
            const englishMonth = '${course.date.month}';
            const day = courseInfo.day;
            const year = courseInfo.year;
            
            const frenchDay = translateDayToFrench(englishDay);
            const frenchMonth = translateMonthToFrench(englishMonth);
            
            fullDateElement.textContent = frenchDay + ' ' + day + ' ' + frenchMonth + ' ' + year;
        }
        
        const messageInput = document.getElementById('messageInput');
        const chatContainer = document.getElementById('chat-messages');
        
        if (messageInput) {
            messageInput.focus();
        }
        
        if (chatContainer) {
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
        
        // Initialiser la carte
        initMap();
    });
     
    // ===== CARTE GPS INTERACTIVE =====
    
    function initMap() {
        // Adresse de la course depuis le serveur
        const courseLocation = '${course.city}';
        console.log('🗺️ Initialisation carte pour:', courseLocation);
        
        // Coordonnées par défaut (centre de la France) en attendant le géocodage
        let defaultLat = 46.603354;
        let defaultLng = 1.888334;
        let defaultZoom = 6;
        
        // Initialiser la carte Leaflet
        const map = L.map('map').setView([defaultLat, defaultLng], defaultZoom);
        
        // Ajouter les tuiles OpenStreetMap
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 19
        }).addTo(map);
        
        // Variable pour stocker le marqueur
        let marker = null;
        
        // Géocoder l'adresse avec Nominatim
        if (courseLocation && courseLocation.trim() !== '') {
            console.log('🔍 Géocodage de l\'adresse:', courseLocation);
            
            // URL de l'API Nominatim (géocodage gratuit)
            const geocodeUrl = 'https://nominatim.openstreetmap.org/search?format=json&q=' + 
                              encodeURIComponent(courseLocation) + '&countrycodes=fr&limit=1';
            
            fetch(geocodeUrl)
                .then(response => response.json())
                .then(data => {
                    console.log('📍 Résultat géocodage:', data);
                    
                    if (data && data.length > 0) {
                        const result = data[0];
                        const lat = parseFloat(result.lat);
                        const lng = parseFloat(result.lon);
                        
                        console.log(`✅ Adresse trouvée: ${lat}, ${lng}`);
                        
                        // Centrer la carte sur l'adresse trouvée
                        map.setView([lat, lng], 15);
                        
                        // Ajouter un marqueur coloré
                        marker = L.marker([lat, lng], {
                            icon: L.divIcon({
                                className: 'custom-marker',
                                html: '<div style="background: linear-gradient(135deg, #6a82fb 0%, #fc5c7d 100%); width: 30px; height: 30px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(0,0,0,0.3); display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 16px;">📍</div>',
                                iconSize: [36, 36],
                                iconAnchor: [18, 36]
                            })
                        }).addTo(map);
                        
                        // Popup simple avec seulement l'adresse
                        const popupContent = '<div style="text-align: center; padding: 8px; min-width: 150px; font-family: Arial, sans-serif;">' +
                            '<div style="color: #666; font-size: 0.9rem;">' +
                            '📍 ' + courseLocation + '</div>' +
                            '</div>';
                        marker.bindPopup(popupContent).openPopup();
                        
                    } else {
                        console.warn('⚠️ Adresse non trouvée par le géocodage');
                        showMapError('Adresse non trouvée');
                    }
                })
                .catch(error => {
                    console.error('❌ Erreur géocodage:', error);
                    showMapError('Erreur de géolocalisation');
                });
        } else {
            console.warn('⚠️ Aucune adresse fournie');
            showMapError('Adresse non disponible');
        }
        
        // Fonction pour afficher les erreurs sur la carte
        function showMapError(message) {
            // Ajouter un marqueur par défaut avec message d'erreur
            marker = L.marker([defaultLat, defaultLng]).addTo(map);
            marker.bindPopup(
                '<div style="text-align: center; color: #ff6a88;">' +
                '❌ ' + message + '<br>' +
                '<small>Localisation indisponible</small>' +
                '</div>'
            ).openPopup();
        }
    }
     
    // ===== FONCTIONNALITÉS EXISTANTES =====
    
    // Afficher/masquer la liste des participants
    document.getElementById('show-participants').onclick = function() {
        document.getElementById('participants-popup').style.display = 'flex';
    };

    // Popup de participation
    function openParticipationPopup() {
        document.getElementById('participationPopup').style.display = 'flex';
    }

    function closeParticipationPopup() {
        document.getElementById('participationPopup').style.display = 'none';
    }

    // Fermer popup si clic sur overlay
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('popup-overlay')) {
            e.target.style.display = 'none';
        }
    });

    // Modal d'authentification (si nécessaire)
    function openAuthModal() {
        // Rediriger vers la page de connexion ou afficher un modal
        window.location.href = '${pageContext.request.contextPath}/login';
    }
</script>
</body>
</html> 