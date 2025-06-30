# 📧 Guide Complet - Configuration Email Color Run

## 🚀 Démarrage rapide

### Option 1 : Script interactif (Recommandé)
```bash
chmod +x test-all-email-configs.sh
./test-all-email-configs.sh
```

### Option 2 : Configuration manuelle
Choisissez votre fournisseur ci-dessous.

---

## 🔵 OUTLOOK/HOTMAIL (Le plus simple)

### ✅ Avantages
- Pas de configuration 2FA nécessaire
- Utilise votre mot de passe normal
- Configuration la plus simple

### 📝 Configuration
```bash
export OUTLOOK_USERNAME=votre-email@outlook.com
export OUTLOOK_PASSWORD=votre-mot-de-passe

./start-email-config-outlook.sh
```

### 🔧 Configuration manuelle
```bash
mvn tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.username=votre-email@outlook.com \
  -Dcolorrun.email.password=votre-mot-de-passe \
  -Dcolorrun.email.smtp.host=smtp.office365.com \
  -Dcolorrun.email.smtp.port=587
```

---

## 🔴 GMAIL

### ⚠️ Prérequis
1. Activer l'authentification à 2 facteurs
2. Créer un mot de passe d'application

### 📝 Configuration
```bash
export GMAIL_USERNAME=votre-email@gmail.com
export GMAIL_APP_PASSWORD=votre-mot-de-passe-app

./start-email-config.sh
```

### 🔧 Étapes détaillées
1. **Aller sur** : [Sécurité du compte Google](https://myaccount.google.com/security)
2. **Activer** : Validation en 2 étapes (si pas déjà fait)
3. **Aller sur** : Mots de passe d'application
4. **Sélectionner** : Mail
5. **Copier** le mot de passe généré (16 caractères)

---

## 🟣 YAHOO MAIL

### ⚠️ Prérequis
1. Activer l'authentification à 2 facteurs
2. Créer un mot de passe d'application

### 📝 Configuration
```bash
export YAHOO_USERNAME=votre-email@yahoo.com
export YAHOO_APP_PASSWORD=votre-mot-de-passe-app

./start-email-config-yahoo.sh
```

### 🔧 Étapes détaillées
1. **Aller sur** : [Sécurité Yahoo](https://login.yahoo.com/account/security)
2. **Activer** : Vérification en 2 étapes
3. **Générer** : Mot de passe d'application
4. **Utiliser** ce mot de passe dans la configuration

---

## ⚙️ SERVEUR SMTP PERSONNALISÉ

### 📝 Configuration complète
```bash
export SMTP_HOST=smtp.votre-serveur.com
export SMTP_PORT=587
export SMTP_USERNAME=votre-username
export SMTP_PASSWORD=votre-password
export SMTP_FROM=noreply@votre-domaine.com
export SMTP_TLS=true

./start-email-config-custom.sh
```

### 🔧 Exemples de serveurs
| Fournisseur | Serveur SMTP | Port | TLS |
|-------------|--------------|------|-----|
| **OVH** | ssl0.ovh.net | 587 | Oui |
| **1&1** | smtp.1and1.com | 587 | Oui |
| **Gandi** | mail.gandi.net | 587 | Oui |
| **Free** | smtp.free.fr | 25 | Non |
| **Orange** | smtp.orange.fr | 25 | Non |

---

## 🧪 MODE TEST (Sans envoi réel)

### 📝 Pour tester sans configurer d'email
```bash
mvn tomcat7:run -Dcolorrun.email.enabled=false
```

Les emails seront affichés dans la console au lieu d'être envoyés.

---

## 🔍 TESTS ET DIAGNOSTIC

### 1. Test de configuration
**URL** : `http://localhost:8080/runton-color/test-email`

### 2. Test d'inscription
1. Aller sur `http://localhost:8080/runton-color/`
2. Créer un nouveau compte
3. Vérifier la réception de l'email

### 3. Vérification des logs
Surveillez la console pour les messages :
- ✅ `Email envoyé avec succès`
- ❌ `Erreur lors de l'envoi d'email`

---

## 🐛 DÉPANNAGE

### Problème : "Authentification failed"
**Solutions** :
- Vérifier username/password
- Pour Gmail/Yahoo : utiliser mot de passe d'application
- Vérifier que 2FA est activé

### Problème : "Connection timeout"
**Solutions** :
- Vérifier la connexion internet
- Tester un autre port (25, 465, 587)
- Vérifier pare-feu/antivirus

### Problème : "Email not received"
**Solutions** :
- Vérifier dossier spam/indésirables
- Attendre quelques minutes
- Tester avec une autre adresse email

---

## 📊 TABLEAU COMPARATIF

| Fournisseur | Difficulté | Fiabilité | Vitesse | 2FA requis |
|-------------|------------|-----------|---------|------------|
| **Outlook** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ❌ |
| **Gmail** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ✅ |
| **Yahoo** | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ✅ |
| **Perso** | ⭐⭐ | Variable | Variable | Variable |

---

## 🎯 RECOMMANDATION

**Pour débuter** : Utilisez **Outlook** - c'est le plus simple !

1. Créez un compte gratuit sur [outlook.com](https://outlook.com)
2. Lancez le script interactif : `./test-all-email-configs.sh`
3. Choisissez option 1 (Outlook)
4. Testez avec `/test-email`

---

## 📞 SUPPORT

Si aucune option ne fonctionne :
1. Vérifiez les logs dans la console
2. Testez d'abord avec `/test-email`
3. Essayez le mode test sans envoi réel 