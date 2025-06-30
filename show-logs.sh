#!/bin/bash

# Script pour afficher les logs Color Run en temps réel avec couleurs

echo "🔍 VISUALISEUR DE LOGS COLOR RUN"
echo "==============================="
echo ""
echo "Ce script va démarrer l'application ET afficher les logs en temps réel."
echo "Vous verrez tous les détails de chaque opération (inscription, emails, etc.)"
echo ""
echo "📧 Pour tester l'email ensuite :"
echo "   👉 http://localhost:8080/runton-color/test-email"
echo ""
echo "📝 Pour faire une inscription :"
echo "   👉 http://localhost:8080/runton-color/"
echo ""
echo -n "Appuyez sur Entrée pour démarrer..."
read

# Vérifier si l'application est déjà lancée
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
    echo "⚠️  L'application semble déjà en cours d'exécution sur le port 8080"
    echo "Arrêtez-la d'abord avec Ctrl+C dans l'autre terminal"
    exit 1
fi

echo ""
echo "🚀 Démarrage de l'application..."
echo "Les logs apparaîtront ci-dessous avec des couleurs :"
echo ""
echo "🔵 INFO  - Informations générales"
echo "🟢 SUCCESS - Opérations réussies"
echo "🟡 WARN  - Avertissements"
echo "🔴 ERROR - Erreurs"
echo "🟣 DEBUG - Détails techniques"
echo "🔶 EMAIL - Opérations d'email"
echo "⚪ DATABASE - Base de données"
echo ""
echo "═════════════════════════════════════════════════════════════════"
echo ""

# Démarrage avec logs colorés
# Si vous avez configuré l'email, ajoutez vos paramètres ici
mvn tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.username="${EMAIL_USERNAME:-}" \
  -Dcolorrun.email.password="${EMAIL_PASSWORD:-}" \
  -Dcolorrun.email.smtp.host="${EMAIL_HOST:-smtp.gmail.com}" \
  -Dcolorrun.email.smtp.port="${EMAIL_PORT:-587}" 