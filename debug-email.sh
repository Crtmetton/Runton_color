#!/bin/bash

# Script de debug spécifique pour les problèmes d'email

echo "🔍 DEBUG EMAIL COLOR RUN"
echo "========================="
echo ""

echo "1. 🧪 Test de la configuration actuelle..."
echo ""

# Vérifier les variables d'environnement
echo "📊 Variables d'environnement :"
echo "  EMAIL_USERNAME: ${EMAIL_USERNAME:-❌ NON DÉFINI}"
echo "  EMAIL_PASSWORD: ${EMAIL_PASSWORD:-❌ NON DÉFINI}"
echo "  EMAIL_HOST: ${EMAIL_HOST:-gmail par défaut}"
echo "  EMAIL_PORT: ${EMAIL_PORT:-587 par défaut}"
echo ""

# Vérifier si l'application est lancée
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
    echo "✅ Application en cours d'exécution sur le port 8080"
    echo ""
    echo "🧪 Tests disponibles :"
    echo "  👉 Test email: http://localhost:8080/runton-color/test-email"
    echo "  👉 Inscription: http://localhost:8080/runton-color/"
    echo ""
    
    echo "📋 Étapes de test recommandées :"
    echo "1. Aller sur /test-email pour vérifier la config"
    echo "2. Faire une inscription pour tester l'email de vérification"
    echo "3. Surveiller les logs ci-dessous pour voir les détails"
    echo ""
    echo "═════════════════════════════════════════════════════════════════"
    echo "🔍 LOGS EN TEMPS RÉEL :"
    echo "═════════════════════════════════════════════════════════════════"
    echo ""
    
    # Suivre les logs de Tomcat
    tail -f target/tomcat/logs/catalina.*.log 2>/dev/null || echo "⚠️ Logs Tomcat non trouvés"
    
else
    echo "❌ Application non démarrée"
    echo ""
    echo "🚀 Pour démarrer avec logs détaillés :"
    echo "  ./show-logs.sh"
    echo ""
    echo "🚀 Ou démarrage manuel :"
    echo "  Configurez d'abord :"
    echo "    export EMAIL_USERNAME=votre-email@outlook.com"
    echo "    export EMAIL_PASSWORD=votre-mot-de-passe"
    echo ""
    echo "  Puis lancez :"
    echo "    mvn tomcat7:run -Dcolorrun.email.enabled=true \\"
    echo "      -Dcolorrun.email.username=\$EMAIL_USERNAME \\"
    echo "      -Dcolorrun.email.password=\$EMAIL_PASSWORD"
fi 