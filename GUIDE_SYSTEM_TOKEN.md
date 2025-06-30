# 🔐 GUIDE D'UTILISATION DU SYSTÈME DE TOKEN

## Vue d'ensemble

Le système de token intégré permet de gérer l'authentification et les autorisations sur toutes les pages de l'application **Runton Color**.

## 📁 Architecture

### 1. Classes principales

- **`UserToken`** : Encapsule les informations utilisateur (ID, nom, email, rôle, etc.)
- **`TokenManager`** : Gestionnaire centralisé pour créer, valider et détruire les tokens
- **`AuthenticationFilter`** : Filtre qui ajoute automatiquement les informations token sur toutes les requêtes

### 2. Flux d'authentification

```
1. Utilisateur se connecte → AuthServlet
2. AuthServlet valide les identifiants → UserService
3. Si valide → TokenManager.createToken() → Token stocké en session
4. Sur chaque requête → AuthenticationFilter ajoute les infos token
5. Servlet peut utiliser TokenManager.isAuthenticated(), isAdmin(), etc.
6. Déconnexion → LogoutServlet → TokenManager.destroyToken()
```

## 🔧 Utilisation dans les Servlets

### Vérifications d'authentification

```java
// Vérifier si utilisateur connecté
if (!TokenManager.isAuthenticated(request)) {
    response.sendRedirect(request.getContextPath() + "/?error=auth_required");
    return;
}

// Vérifier rôle administrateur
if (TokenManager.isAdmin(request)) {
    // Accès admin
}

// Vérifier rôle organisateur (inclut admin)
if (TokenManager.isOrganizer(request)) {
    // Accès organisateur
}

// Récupérer le token complet
UserToken token = TokenManager.getToken(request);
if (token != null) {
    String name = token.getFullName();
    String email = token.getEmail();
    String role = token.getRole();
}
```

### Méthodes de protection

```java
// Rediriger si non authentifié
if (!TokenManager.requireAuthentication(request, response, "/login")) {
    return;
}

// Vérifier permissions organisateur (avec gestion d'erreur automatique)
try {
    if (!TokenManager.requireOrganizerRole(request, response)) {
        return;
    }
} catch (Exception e) {
    // Gérer l'erreur
}

// Vérifier permissions admin
try {
    if (!TokenManager.requireAdminRole(request, response)) {
        return;
    }
} catch (Exception e) {
    // Gérer l'erreur
}
```

## 🎨 Utilisation dans les JSP/Vues

Le filtre `AuthenticationFilter` ajoute automatiquement ces attributs à toutes les requêtes :

### Attributs disponibles

```jsp
<%-- Informations de base --%>
${isAuthenticated}     <!-- Boolean: true si connecté -->
${userName}           <!-- String: nom complet -->
${userRole}           <!-- String: rôle en français -->

<%-- Vérifications de rôle --%>
${isAdmin}            <!-- Boolean: true si admin -->
${isOrganizer}        <!-- Boolean: true si organisateur ou admin -->
${isUser}             <!-- Boolean: true si utilisateur simple -->

<%-- Token complet --%>
${userToken}          <!-- UserToken: objet complet -->
${currentUser}        <!-- UserToken: alias -->
```

### Exemple d'utilisation dans JSP

```jsp
<%-- Affichage conditionnel selon l'authentification --%>
<c:choose>
    <c:when test="${isAuthenticated}">
        <div class="user-welcome">
            Bienvenue <strong>${userName}</strong> (${userRole})
        </div>
        
        <%-- Menu selon le rôle --%>
        <c:if test="${isAdmin}">
            <a href="/admin">🛠️ Administration</a>
        </c:if>
        
        <c:if test="${isOrganizer}">
            <a href="/creation-course">➕ Créer une course</a>
            <a href="/mes-courses">🏃‍♂️ Mes courses</a>
        </c:if>
        
        <a href="/logout">🚪 Déconnexion</a>
    </c:when>
    <c:otherwise>
        <a href="/#login">🔐 Se connecter</a>
        <a href="/#register">📝 S'inscrire</a>
    </c:otherwise>
</c:choose>
```

## 🔒 Sécurité

### Expiration des tokens

- **Durée de vie** : 24 heures
- **Mise à jour automatique** : à chaque requête
- **Nettoyage** : tokens expirés supprimés automatiquement

### Validation

- Vérification de l'expiration à chaque accès
- Invalidation lors de la déconnexion
- Token lié à la session HTTP

## 📋 Rôles supportés

| Rôle | Code | Permissions |
|------|------|-------------|
| **Utilisateur** | `USER` | Voir courses, participer, profil |
| **Organisateur** | `ORGANISATEUR` | Toutes permissions utilisateur + créer courses |
| **Administrateur** | `ADMIN` | Toutes permissions + gestion utilisateurs |

## 🔧 Configuration

### Dans web.xml

Le filtre est automatiquement configuré :

```xml
<filter>
    <filter-name>AuthenticationFilter</filter-name>
    <filter-class>com.colorrun.filter.AuthenticationFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>AuthenticationFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

## 📊 Exemple d'utilisation complète

### Dans un Servlet

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    // Les informations token sont automatiquement ajoutées par le filtre
    
    // Vérifier l'authentification
    if (!TokenManager.isAuthenticated(request)) {
        response.sendRedirect(request.getContextPath() + "/?error=auth_required");
        return;
    }
    
    // Récupérer les informations utilisateur
    UserToken token = TokenManager.getToken(request);
    Logger.info("MyServlet", "Accès de " + token.getFullName() + " (" + token.getRole() + ")");
    
    // Logique spécifique selon le rôle
    if (token.isAdmin()) {
        // Traitement admin
        request.setAttribute("adminData", getAdminData());
    } else if (token.isOrganizer()) {
        // Traitement organisateur
        request.setAttribute("organizerData", getOrganizerData(token.getUserId()));
    } else {
        // Traitement utilisateur standard
        request.setAttribute("userData", getUserData(token.getUserId()));
    }
    
    // Forward vers la vue
    request.getRequestDispatcher("/WEB-INF/views/my-page.jsp").forward(request, response);
}
```

### Dans la JSP correspondante

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Ma Page - Runton Color</title>
</head>
<body>
    <%-- Informations utilisateur automatiquement disponibles --%>
    <header>
        <h1>Bienvenue ${userName}</h1>
        <span class="role-badge">${userRole}</span>
    </header>
    
    <%-- Contenu conditionnel --%>
    <main>
        <c:if test="${isAdmin}">
            <section class="admin-section">
                <h2>🛠️ Administration</h2>
                <%-- Contenu admin --%>
            </section>
        </c:if>
        
        <c:if test="${isOrganizer}">
            <section class="organizer-section">
                <h2>🏃‍♂️ Mes courses</h2>
                <%-- Contenu organisateur --%>
            </section>
        </c:if>
        
        <section class="user-section">
            <h2>👤 Mon profil</h2>
            <p>Email: ${userToken.email}</p>
            <p>Membre depuis: ${userToken.createdAt}</p>
            <%-- Contenu utilisateur --%>
        </section>
    </main>
</body>
</html>
```

## 🚀 Avantages

1. **Centralisation** : Un seul point de gestion pour l'authentification
2. **Automatisation** : Informations disponibles sur toutes les pages
3. **Sécurité** : Validation et expiration automatiques
4. **Simplicité** : Interface claire et cohérente
5. **Performance** : Stockage en session, pas de base de données à chaque requête
6. **Flexibilité** : Support de tous les rôles et permissions

## 🛠️ Mise en œuvre dans les Servlets existants

Pour adapter un servlet existant :

1. **Ajouter l'import** : `import com.colorrun.security.TokenManager;`
2. **Remplacer les vérifications session** par les méthodes TokenManager
3. **Utiliser les attributs automatiques** dans les JSP
4. **Supprimer les vérifications manuelles** de rôle

Le système est **rétrocompatible** et peut coexister avec l'ancien système le temps de la migration. 