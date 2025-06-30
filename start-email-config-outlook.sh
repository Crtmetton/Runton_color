#!/bin/bash

# Script de démarrage avec configuration Outlook/Hotmail pour Color Run
echo "🚀 Démarrage de Color Run avec Outlook/Hotmail..."

# Vérification des variables d'environnement
if [ -z "$OUTLOOK_USERNAME" ]; then
    echo "⚠️  OUTLOOK_USERNAME non défini"
    echo "Exemple : export OUTLOOK_USERNAME=votre-email@outlook.com"
    echo "Ou     : export OUTLOOK_USERNAME=votre-email@hotmail.com"
    exit 1
fi

if [ -z "$OUTLOOK_PASSWORD" ]; then
    echo "⚠️  OUTLOOK_PASSWORD non défini"
    echo "Utilisez votre mot de passe Outlook normal :"
    echo "export OUTLOOK_PASSWORD=votre-mot-de-passe"
    exit 1
fi

echo "📧 Configuration email Outlook :"
echo "  - Username: $OUTLOOK_USERNAME"
echo "  - Service: Outlook SMTP"
echo "  - Port: 587 (TLS)"
echo ""

# Nettoyage et compilation
echo "🔧 Compilation du projet..."
mvn clean package -q

# Démarrage avec configuration Outlook
echo "🌟 Démarrage du serveur avec emails Outlook activés..."
mvn tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.username="$OUTLOOK_USERNAME" \
  -Dcolorrun.email.password="$OUTLOOK_PASSWORD" \
  -Dcolorrun.email.smtp.host=smtp.office365.com \
  -Dcolorrun.email.smtp.port=587 \
  -Dcolorrun.email.from="$OUTLOOK_USERNAME" \
  -Dcolorrun.email.fromName="Color Run" 