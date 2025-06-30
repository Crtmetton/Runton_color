# Configuration des emails - Color Run

Ce guide explique comment configurer l'envoi d'emails dans l'application Color Run.

## 📧 Fonctionnalités email implémentées

1. **Email de vérification** lors de l'inscription
2. **Email de bienvenue** après activation du compte
3. **Email de dossard** avec PDF en pièce jointe
4. **Email de confirmation** d'inscription à une course
5. **Email de rappel** avant une course

## ⚙️ Configuration

### Variables d'environnement Java

Pour configurer l'envoi d'emails, définissez ces propriétés système :

```bash
# Configuration SMTP
-Dcolorrun.email.smtp.host=smtp.gmail.com
-Dcolorrun.email.smtp.port=587
-Dcolorrun.email.username=votre-email@gmail.com
-Dcolorrun.email.password=votre-mot-de-passe-app
-Dcolorrun.email.from=noreply@colorrun.com
-Dcolorrun.email.fromName="Color Run"
-Dcolorrun.email.enabled=true
```

### Configuration Gmail

1. **Activer l'authentification à 2 facteurs** sur votre compte Gmail

2. **Générer un mot de passe d'application** :
   - Allez dans les paramètres Gmail → Sécurité
   - Authentification à 2 facteurs → Mots de passe des applications
   - Sélectionnez "Mail" et générez un mot de passe
   - Utilisez ce mot de passe pour `colorrun.email.password`

### Configuration alternative (autres providers)

```bash
# Exemple pour Outlook/Hotmail
-Dcolorrun.email.smtp.host=smtp-mail.outlook.com
-Dcolorrun.email.smtp.port=587

# Exemple pour Yahoo
-Dcolorrun.email.smtp.host=smtp.mail.yahoo.com
-Dcolorrun.email.smtp.port=587
```

## 🚀 Démarrage avec emails

### Option 1: Variables d'environnement

```bash
export JAVA_OPTS="-Dcolorrun.email.enabled=true -Dcolorrun.email.username=votre-email@gmail.com -Dcolorrun.email.password=votre-mot-de-passe-app"
./start-tomcat.sh
```

### Option 2: Ligne de commande Maven

```bash
mvn clean package tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.username=votre-email@gmail.com \
  -Dcolorrun.email.password=votre-mot-de-passe-app
```

### Option 3: Désactiver les emails (développement)

```bash
mvn clean package tomcat7:run -Dcolorrun.email.enabled=false
```

## 📋 Test de configuration

L'application inclut une méthode de test :

```java
EmailService emailService = new EmailServiceImpl();
boolean success = emailService.testEmailConfiguration("test@example.com");
```

## 🔧 Dépannage

### Erreur d'authentification
- Vérifiez que l'authentification à 2 facteurs est activée
- Utilisez un mot de passe d'application, pas votre mot de passe habituel
- Vérifiez les variables d'environnement

### Timeout de connexion
```bash
# Augmenter les timeouts
-Dcolorrun.email.smtp.connectiontimeout=30000
-Dcolorrun.email.smtp.timeout=30000
```

### Emails non reçus
- Vérifiez les dossiers spam/indésirables
- Vérifiez que l'adresse `from` est valide
- Testez avec une adresse email différente

## 📝 Templates d'emails

Les templates HTML sont inclus dans le code. Pour personnaliser :

1. Modifiez les méthodes `build*EmailContent()` dans `EmailServiceImpl`
2. Adaptez les styles CSS inline
3. Modifiez les URLs et informations de contact

## 🏗️ Structure du système

```
EmailConfig          → Configuration centralisée
EmailService         → Interface du service
EmailServiceImpl     → Implémentation JavaMail
VerificationTokenService → Gestion des tokens
DossardService       → Génération PDF + QR codes
```

## 💡 Bonnes pratiques

1. **Ne jamais commiter** les mots de passe dans le code
2. **Utiliser des variables d'environnement** pour la configuration
3. **Tester la configuration** avant le déploiement
4. **Surveiller les logs** d'envoi d'emails
5. **Implémenter une limite de débit** pour éviter le spam

## 🔗 Liens utiles

- [Configuration Gmail SMTP](https://support.google.com/mail/answer/7126229)
- [JavaMail API Documentation](https://javaee.github.io/javamail/)
- [Mots de passe d'application Gmail](https://support.google.com/accounts/answer/185833) 