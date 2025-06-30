#!/bin/bash

# Script de démarrage avec configuration email pour Color Run
echo "🚀 Démarrage de Color Run avec configuration email..."

# Vérification des variables d'environnement
if [ -z "$GMAIL_USERNAME" ]; then
    echo "⚠️  GMAIL_USERNAME non défini"
    echo "Exemple : export GMAIL_USERNAME=votre-email@gmail.com"
    exit 1
fi

if [ -z "$GMAIL_APP_PASSWORD" ]; then
    echo "⚠️  GMAIL_APP_PASSWORD non défini"
    echo "Créez un mot de passe d'application Gmail et définissez :"
    echo "export GMAIL_APP_PASSWORD=votre-mot-de-passe-app"
    exit 1
fi

echo "📧 Configuration email :"
echo "  - Username: $GMAIL_USERNAME"
echo "  - Service: Gmail SMTP"
echo "  - Port: 587 (TLS)"
echo ""

# Nettoyage et compilation
echo "🔧 Compilation du projet..."
mvn clean package -q

# Démarrage avec configuration email
echo "🌟 Démarrage du serveur avec emails activés..."
mvn tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.username="$GMAIL_USERNAME" \
  -Dcolorrun.email.password="$GMAIL_APP_PASSWORD" \
  -Dcolorrun.email.smtp.host=smtp.gmail.com \
  -Dcolorrun.email.smtp.port=587 \
  -Dcolorrun.email.from="$GMAIL_USERNAME" \
  -Dcolorrun.email.fromName="Color Run" 