#!/bin/bash

echo "🚀 DÉMARRAGE TOMCAT EN MODE PRODUCTION (LOGS RÉDUITS)"
echo "=================================================="

# Arrêter les processus existants
echo "🔄 Arrêt des processus existants..."
pkill -f "tomcat" 2>/dev/null || true
pkill -f "h2" 2>/dev/null || true
sleep 2

# Nettoyer les logs
echo "🧹 Nettoyage des logs..."
> tomcat.log

echo "⚡ Démarrage Tomcat avec logs réduits pour de meilleures performances..."
echo "📝 Mode production : Seules les erreurs et warnings sont affichés"
echo "🌐 L'application sera disponible sur : http://localhost:8080"
echo ""

# Démarrer avec le niveau de log en mode production (1)
export MAVEN_OPTS="-Dlog.level=1 -Xms512m -Xmx1024m"

# Démarrer Tomcat avec Maven en arrière-plan et rediriger les logs
mvn tomcat7:run >> tomcat.log 2>&1 &

echo "✅ Tomcat démarré en arrière-plan"
echo "📊 Suivi des logs : tail -f tomcat.log"
echo "⏱️  Temps de démarrage typique : 10-15 secondes"
echo ""
echo "💡 Pour activer plus de logs pendant le développement :"
echo "   ./start-tomcat.sh (mode normal avec tous les logs)"
echo ""

# Attendre un peu puis afficher le statut
sleep 5
echo "🔍 Vérification du statut..."
if pgrep -f "tomcat" > /dev/null; then
    echo "✅ Tomcat semble démarré correctement"
    echo "🌐 Testez l'application : http://localhost:8080"
else
    echo "❌ Problème de démarrage, consultez les logs :"
    echo "   tail -f tomcat.log"
fi 