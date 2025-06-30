#!/bin/bash

# Script de démarrage avec configuration Yahoo Mail pour Color Run
echo "🚀 Démarrage de Color Run avec Yahoo Mail..."

# Vérification des variables d'environnement
if [ -z "$YAHOO_USERNAME" ]; then
    echo "⚠️  YAHOO_USERNAME non défini"
    echo "Exemple : export YAHOO_USERNAME=votre-email@yahoo.com"
    echo "Ou     : export YAHOO_USERNAME=votre-email@yahoo.fr"
    exit 1
fi

if [ -z "$YAHOO_APP_PASSWORD" ]; then
    echo "⚠️  YAHOO_APP_PASSWORD non défini"
    echo "Créez un mot de passe d'application Yahoo et définissez :"
    echo "export YAHOO_APP_PASSWORD=votre-mot-de-passe-app"
    echo ""
    echo "💡 Pour Yahoo, vous devez :"
    echo "1. Aller dans Sécurité du compte Yahoo"
    echo "2. Activer l'authentification à 2 facteurs"
    echo "3. Générer un mot de passe d'application"
    exit 1
fi

echo "📧 Configuration email Yahoo :"
echo "  - Username: $YAHOO_USERNAME"
echo "  - Service: Yahoo SMTP"
echo "  - Port: 587 (TLS)"
echo ""

# Nettoyage et compilation
echo "🔧 Compilation du projet..."
mvn clean package -q

# Démarrage avec configuration Yahoo
echo "🌟 Démarrage du serveur avec emails Yahoo activés..."
mvn tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.username="$YAHOO_USERNAME" \
  -Dcolorrun.email.password="$YAHOO_APP_PASSWORD" \
  -Dcolorrun.email.smtp.host=smtp.mail.yahoo.com \
  -Dcolorrun.email.smtp.port=587 \
  -Dcolorrun.email.from="$YAHOO_USERNAME" \
  -Dcolorrun.email.fromName="Color Run" 