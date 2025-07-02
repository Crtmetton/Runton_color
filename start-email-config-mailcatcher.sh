#!/bin/bash

# Script de démarrage avec MailCatcher pour Color Run
echo "🎣 Démarrage de Color Run avec MailCatcher..."

# Vérifier si Docker est disponible
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé. Utilisez une autre méthode."
    exit 1
fi

# Démarrer MailCatcher si pas déjà lancé
if ! docker ps | grep -q mailcatcher; then
    echo "🐳 Démarrage de MailCatcher..."
    docker run -d -p 1080:1080 -p 1025:1025 --name mailcatcher schickling/mailcatcher
    echo "✅ MailCatcher démarré"
    echo "🌐 Interface web : http://localhost:1080"
else
    echo "✅ MailCatcher déjà en cours d'exécution"
fi

echo ""
echo "📧 Configuration email :"
echo "  - Serveur SMTP : localhost"
echo "  - Port : 1025"
echo "  - Interface web : http://localhost:1080"
echo ""

# Nettoyage et compilation
echo "🔧 Compilation du projet..."
mvn clean package -q

# Démarrage avec configuration MailCatcher
echo "🌟 Démarrage du serveur avec MailCatcher..."
mvn tomcat7:run \
  -Dcolorrun.email.enabled=true \
  -Dcolorrun.email.smtp.host=localhost \
  -Dcolorrun.email.smtp.port=1025 \
  -Dcolorrun.email.smtp.auth=false \
  -Dcolorrun.email.smtp.starttls.enable=false \
  -Dcolorrun.email.from=noreply@colorrun.com \
  -Dcolorrun.email.fromName="Color Run" 