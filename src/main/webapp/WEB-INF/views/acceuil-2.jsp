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
            <button class="search-button">Voir les courses</button>
            <button class="search-button secondary">Devenir bénévole</button>
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

        <!-- Prochaines étapes colorées -->
        <section class="courses-container">
            <h2 style="text-align:center;margin-bottom:32px;">Prochaines étapes colorées</h2>
            <div id="etapes-color-list" style="display:flex;justify-content:center;gap:60px;">
                <!-- Les étapes seront générées dynamiquement ici -->
            </div>
            <div style="display:flex; justify-content:center; gap:16px; margin-top:32px;">
                <button class="search-button">Voir toutes les étapes</button>
            </div>
        </section>

        <!-- Témoignages / Section infos -->
        <section class="courses-container">
            <div style="display:flex;gap:60px;align-items:center;">
                <div style="width:320px;height:180px;background:#e2e7ff;border-radius:20px;"></div>
                <div style="margin:0 48px;">
                    <h3>Titre</h3>
                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                    </p>
                    <div style="display:flex; justify-content:center; gap:16px;">
                        <button class="search-button">Découvrir</button>
                        <button class="search-button secondary">En savoir plus</button>
                    </div>
                </div>
            </div>
        </section>
        <section class="courses-container">
            <div style="display:flex;gap:60px;align-items:center;">
                <div style="margin:0 48px;">
                    <h3>Titre</h3>
                    <p>
                        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                    </p>
                    <div style="display:flex; justify-content:center; gap:16px; margin-top:32px;">
                        <button class="search-button">Découvrir</button>
                        <button class="search-button secondary">En savoir plus</button>
                    </div>
                </div>
                <div style="width:320px;height:180px;background:#e2e7ff;border-radius:20px;"></div>
            </div>
        </section>
    </div>
</div>
<!-- Fin du bloc blanc -->

<!-- Footer -->
<!-- Inclusion du footer -->
<%@ include file="/WEB-INF/views/fragments/footer.jsp" %>

<script>

    // --- Variables dynamiques pour les étapes colorées ---
    const etapes = [
        { ville: "Paris", date: "12/06/2024" },
        { ville: "Lyon", date: "19/06/2024" },
        { ville: "Marseille", date: "26/06/2024" }
        // ...ajoute d'autres étapes ici...
    ];

    const etapesColorList = document.getElementById('etapes-color-list');
    if (etapesColorList) {
        etapesColorList.innerHTML = etapes.map(etape => `
        <div style="text-align:center;">
            <div style="width:80px;height:80px;background:#e2e7ff;border-radius:15px;margin:0 auto 20px;"></div>
            <div style="margin:0 24px;">${etape.ville}<br>${etape.date}</div>
        </div>
    `).join('');
    }
</script>
</body>
</html>
