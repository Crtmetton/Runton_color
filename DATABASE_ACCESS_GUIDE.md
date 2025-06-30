# 🗄️ GUIDE D'ACCÈS À LA BASE DE DONNÉES COLOR RUN

## Configuration de la Base

- **Type** : H2 Database (embarquée)
- **Fichier** : `dbFiles/Runton_color_Prod.mv.db`
- **Utilisateur** : `Runton`
- **Mot de passe** : *(vide)*

---

## 🚀 Méthodes d'Accès

### 1. **Console Web H2** *(Recommandée)*
```bash
./access-database.sh
```
- Interface graphique web sur http://localhost:8082
- Exploration facile des tables
- Exécution de requêtes SQL
- Visualisation des données

### 2. **Requêtes Rapides**
```bash
./query-database.sh
```
- Menu interactif en ligne de commande
- Requêtes prédéfinies pour les tables principales
- Statistiques rapides
- Requêtes personnalisées

### 3. **Script Original**
```bash
./db-console.sh
```
- Script original du projet
- Console H2 basique

---

## 📊 Structure des Tables

### 👥 **USERS** - Utilisateurs
```sql
SELECT * FROM USERS;
```
- ID, FIRST_NAME, LAST_NAME, EMAIL, ROLE, ENABLED, PASSWORD_HASH

### 🏃 **COURSES** - Courses
```sql
SELECT * FROM COURSES;
```
- ID, NAME, DESCRIPTION, CITY, DATE, DISTANCE, MAX_PARTICIPANTS

### 🎯 **PARTICIPATIONS** - Inscriptions
```sql
SELECT * FROM PARTICIPATIONS;
```
- ID, USER_ID, COURSE_ID, REGISTRATION_DATE

### 🏷️ **DOSSARDS** - Numéros de course
```sql
SELECT * FROM DOSSARDS;
```
- ID, NUMBER, COURSE_ID, USER_ID, STATUS, QR_CODE

### 📧 **VERIFICATION_TOKENS** - Tokens email
```sql
SELECT * FROM VERIFICATION_TOKENS;
```
- ID, USER_ID, TOKEN, EXPIRES_AT, USED

---

## 🔍 Requêtes Utiles

### Voir les utilisateurs avec leurs inscriptions
```sql
SELECT 
    u.FIRST_NAME, 
    u.LAST_NAME, 
    u.EMAIL, 
    c.NAME as COURSE_NAME,
    p.REGISTRATION_DATE
FROM USERS u
JOIN PARTICIPATIONS p ON u.ID = p.USER_ID
JOIN COURSES c ON p.COURSE_ID = c.ID
ORDER BY p.REGISTRATION_DATE DESC;
```

### Statistiques des courses
```sql
SELECT 
    c.NAME,
    c.CITY,
    c.DATE,
    COUNT(p.ID) as PARTICIPANTS_INSCRITS,
    c.MAX_PARTICIPANTS
FROM COURSES c
LEFT JOIN PARTICIPATIONS p ON c.ID = p.COURSE_ID
GROUP BY c.ID, c.NAME, c.CITY, c.DATE, c.MAX_PARTICIPANTS
ORDER BY c.DATE;
```

### Dossards par course
```sql
SELECT 
    c.NAME as COURSE,
    d.NUMBER,
    u.FIRST_NAME || ' ' || u.LAST_NAME as PARTICIPANT,
    d.STATUS
FROM DOSSARDS d
JOIN COURSES c ON d.COURSE_ID = c.ID
JOIN USERS u ON d.USER_ID = u.ID
ORDER BY c.NAME, d.NUMBER;
```

---

## 🛠️ Maintenance

### Sauvegarder la base
```bash
cp dbFiles/Runton_color_Prod.mv.db dbFiles/backup_$(date +%Y%m%d_%H%M%S).mv.db
```

### Voir la taille de la base
```bash
du -h dbFiles/
```

### Nettoyer les tokens expirés
```sql
DELETE FROM VERIFICATION_TOKENS WHERE EXPIRES_AT < NOW();
```

---

## 🚨 Dépannage

### Console H2 ne s'ouvre pas
1. Vérifiez que le port 8082 est libre
2. Téléchargez H2 : `mvn dependency:get -Dartifact=com.h2database:h2:2.1.214`

### Base de données verrouillée
1. Arrêtez l'application web
2. Attendez quelques secondes
3. Relancez la console

### Connexion refusée
- Vérifiez le chemin : `jdbc:h2:./dbFiles/Runton_color_Prod`
- Utilisateur : `Runton`
- Mot de passe : *(laissez vide)*

---

## 📱 Accès Rapide

| Action | Commande |
|--------|----------|
| Console Web | `./access-database.sh` |
| Requêtes Menu | `./query-database.sh` |
| Voir Utilisateurs | `./query-database.sh` → choix 1 |
| Voir Courses | `./query-database.sh` → choix 2 |
| Statistiques | `./query-database.sh` → choix 6 | 