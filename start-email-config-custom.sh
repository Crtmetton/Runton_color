#!/bin/bash

# Script de démarrage avec configuration SMTP personnalisée pour Color Run
echo "🚀 Démarrage de Color Run avec serveur SMTP personnalisé..."

# Configuration par défaut si variables non définies
SMTP_HOST=${SMTP_HOST:-"localhost"}
SMTP_PORT=${SMTP_PORT:-"25"}
SMTP_USERNAME=${SMTP_USERNAME:-""}
SMTP_PASSWORD=${SMTP_PASSWORD:-""}
SMTP_FROM=${SMTP_FROM:-"noreply@colorrun.local"}
SMTP_TLS=${SMTP_TLS:-"false"}

echo "📧 Configuration SMTP personnalisée :"
echo "  - Serveur: $SMTP_HOST:$SMTP_PORT"
echo "  - Username: $SMTP_USERNAME"
echo "  - TLS: $SMTP_TLS"
echo "  - From: $SMTP_FROM"
echo ""

# Nettoyage et compilation
echo "🔧 Compilation du projet..."
mvn clean package -q

# Construction des paramètres
SMTP_PARAMS="-Dcolorrun.email.enabled=true"
SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.smtp.host=$SMTP_HOST"
SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.smtp.port=$SMTP_PORT"
SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.from=$SMTP_FROM"
SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.fromName=Color Run"

# Ajout authentification si fournie
if [ ! -z "$SMTP_USERNAME" ]; then
    SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.username=$SMTP_USERNAME"
fi

if [ ! -z "$SMTP_PASSWORD" ]; then
    SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.password=$SMTP_PASSWORD"
fi

# Configuration TLS
if [ "$SMTP_TLS" = "true" ]; then
    SMTP_PARAMS="$SMTP_PARAMS -Dcolorrun.email.smtp.starttls.enable=true"
fi

# Démarrage
echo "🌟 Démarrage du serveur avec SMTP personnalisé..."
mvn tomcat7:run $SMTP_PARAMS 