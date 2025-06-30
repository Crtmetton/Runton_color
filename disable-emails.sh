#!/bin/bash

echo "📧 DÉSACTIVATION SYSTÈME EMAIL COLOR RUN"
echo "========================================"
echo ""

# Configuration pour désactiver les emails
export COLORRUN_EMAIL_ENABLED=false
export COLORRUN_EMAIL_HOST=""
export COLORRUN_EMAIL_PORT=""
export COLORRUN_EMAIL_USERNAME=""
export COLORRUN_EMAIL_PASSWORD=""

echo "✅ Variables d'environnement configurées :"
echo "   COLORRUN_EMAIL_ENABLED=false"
echo "   Autres variables email vidées"
echo ""

echo "🚀 Démarrage de l'application SANS emails..."
echo "   Les inscriptions fonctionneront normalement"
echo "   Les comptes seront activés automatiquement"
echo "   Aucune tentative d'envoi d'email"
echo ""

# Démarrer l'application avec emails désactivés
mvn tomcat7:run -Dcolorrun.email.enabled=false 