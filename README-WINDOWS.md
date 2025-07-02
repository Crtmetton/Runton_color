# RUNTON COLOR - Scripts Windows

Ce répertoire contient les scripts pour lancer le projet Runton Color sur Windows.

## Prérequis

Avant d'utiliser ces scripts, assurez-vous d'avoir :

1. **Java JDK 8 ou supérieur** installé et configuré dans le PATH
2. **Maven** installé et configuré dans le PATH
3. **Git** (optionnel, pour la gestion de version)

### Vérification des prérequis

Ouvrez une invite de commande et vérifiez :

```cmd
java -version
mvn -version
```

## Scripts disponibles

### 1. `start-project-windows.bat` - Démarrage complet

**Usage :** Double-cliquez sur le fichier ou exécutez dans l'invite de commande

Ce script effectue un démarrage complet du projet avec :
- Arrêt des processus Java/Tomcat existants
- Nettoyage du cache Maven
- Compilation complète du projet
- Vérification et libération du port 8090
- Démarrage de Tomcat

**Utilisez ce script quand :**
- Vous lancez le projet pour la première fois
- Vous avez modifié le code et voulez une compilation complète
- Vous rencontrez des problèmes avec une version en cache

### 2. `start-quick-windows.bat` - Démarrage rapide

**Usage :** Double-cliquez sur le fichier ou exécutez dans l'invite de commande

Ce script effectue un démarrage rapide sans nettoyage :
- Vérification et libération du port 8090
- Démarrage direct de Tomcat

**Utilisez ce script quand :**
- Le projet a déjà été compilé au moins une fois
- Vous voulez un démarrage rapide sans recompilation

### 3. `db-console-windows.bat` - Console de base de données

**Usage :** Double-cliquez sur le fichier ou exécutez dans l'invite de commande

Ce script lance la console web H2 pour accéder à la base de données :
- Télécharge H2 via Maven si nécessaire
- Lance la console web sur http://localhost:8082

**Configuration de connexion :**
- **JDBC URL :** `jdbc:h2:[CHEMIN_DU_PROJET]/dbFiles/Runton_color_Prod`
- **User Name :** `Runton`
- **Password :** (laisser vide)

## Accès à l'application

Une fois lancée, l'application est accessible à l'adresse :
**http://localhost:8090/runton-color**

## Résolution des problèmes

### Erreur "Java n'est pas reconnu"
- Vérifiez que Java est installé et dans le PATH
- Redémarrez l'invite de commande après installation

### Erreur "mvn n'est pas reconnu"
- Vérifiez que Maven est installé et dans le PATH
- Téléchargez Maven depuis https://maven.apache.org/

### Port 8090 déjà utilisé
- Les scripts tentent automatiquement de libérer le port
- Si le problème persiste, redémarrez votre ordinateur

### Erreurs de compilation
- Vérifiez que vous êtes dans le bon répertoire du projet
- Utilisez `start-project-windows.bat` pour un nettoyage complet

### Base de données inaccessible
- Vérifiez que le fichier `dbFiles/Runton_color_Prod.mv.db` existe
- Utilisez `db-console-windows.bat` pour vérifier la connexion

## Structure du projet

```
Runton_color/
├── start-project-windows.bat    # Démarrage complet
├── start-quick-windows.bat      # Démarrage rapide  
├── db-console-windows.bat       # Console base de données
├── start-tomcat.sh             # Script Unix/Linux original
├── db-console.sh               # Console BD Unix/Linux
├── dbFiles/                    # Fichiers de base de données H2
├── src/                        # Code source Java
├── pom.xml                     # Configuration Maven
└── target/                     # Fichiers compilés
```

## Support

Si vous rencontrez des problèmes avec ces scripts, vérifiez :
1. Que tous les prérequis sont installés
2. Que vous êtes dans le bon répertoire
3. Que les ports ne sont pas utilisés par d'autres applications

Pour des problèmes spécifiques au projet, consultez la documentation du code source. 