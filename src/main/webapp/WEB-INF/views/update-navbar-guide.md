# Guide : Mise à jour de la Navbar dans toutes les pages

## Pages à convertir en JSP et mettre à jour :

### 1. courses.html → courses.jsp
```bash
# Renommer le fichier
mv courses.html courses.jsp
```

**Modifications à apporter :**
- Ajouter les directives JSP en haut
- Remplacer la navbar par l'include
- Mettre à jour les liens CSS avec contextPath

### 2. mes-courses.html → mes-courses.jsp
**Changements :**
- Convertir en JSP
- Intégrer la gestion des utilisateurs connectés
- Charger dynamiquement les courses de l'utilisateur

### 3. info-course.html → info-course.jsp
**Changements :**
- Convertir en JSP  
- Ajouter les paramètres de course dynamiques

### 4. creation-course.html → creation-course.jsp
**Changements :**
- Convertir en JSP
- Vérifier que l'utilisateur est organisateur

### 5. liste-organisateur.html → liste-organisateur.jsp
**Changements :**
- Convertir en JSP
- Restreindre l'accès aux administrateurs

## Servlets manquants à créer :

### 1. AgendaServlet
```java
// URL : /agenda
// Fonctionnalité : Affichage du calendrier des événements
```

### 2. MesCourses integration dans ProfileServlet
```java
// Modifier ProfileServlet pour afficher les courses de l'utilisateur
```

## Template de conversion HTML vers JSP :

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>[TITRE] | Color Run</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style-liste-course.css">
    <!-- CSS additionnels -->
</head>
<body>
    <!-- Cercles de fond -->
    <div class="bg-circle1"></div>
    <div class="bg-circle2"></div>
    <div class="bg-circle3"></div>
    
    <!-- Navbar unifiée -->
    <%@ include file="/WEB-INF/views/fragments/navbar.jsp" %>
    
    <!-- CONTENU SPÉCIFIQUE -->
    
    <!-- Footer unifié -->
    <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>
</body>
</html>
```

## Vérifications après conversion :

1. **Tester tous les liens de navigation**
2. **Vérifier l'affichage utilisateur connecté/non connecté**  
3. **Tester les popups de connexion/inscription**
4. **Vérifier que les CSS se chargent correctement**
5. **Tester la responsivité sur mobile**

## URLs de test :
- `/` - Page d'accueil
- `/courses` - Liste des courses  
- `/profile` - Profil utilisateur
- `/organizer/request` - Devenir organisateur
- `/login` - Connexion
- `/register` - Inscription 