#!/bin/bash

echo "🗄️ ACCÈS BASE DE DONNÉES COLOR RUN"
echo "=================================="
echo ""

# Configuration
DB_PATH="./dbFiles/Runton_color_Prod"
H2_JAR="$HOME/.m2/repository/com/h2database/h2/2.1.214/h2-2.1.214.jar"

echo "📊 Bases de données disponibles :"
echo "  📁 Production : Runton_color_Prod.mv.db ($(du -h dbFiles/Runton_color_Prod.mv.db | cut -f1))"
echo "  📁 Dev : Runton_color_dev.mv.db ($(du -h dbFiles/Runton_color_dev.mv.db | cut -f1))"
echo ""

# Vérifier si H2 est disponible
if [ ! -f "$H2_JAR" ]; then
    echo "📦 Téléchargement de H2..."
    mvn dependency:get -Dartifact=com.h2database:h2:2.1.214 -q
fi

echo "🚀 Démarrage de la console H2..."
echo ""
echo "🌐 Console Web : http://localhost:8082"
echo ""
echo "🔧 Configuration de connexion :"
echo "┌─────────────────────────────────────────────────────────────┐"
echo "│ JDBC URL    : jdbc:h2:$PWD/dbFiles/Runton_color_Prod      │"
echo "│ User Name   : Runton                                        │"
echo "│ Password    : (vide)                                        │"
echo "│ Driver Class: org.h2.Driver                                │"
echo "└─────────────────────────────────────────────────────────────┘"
echo ""
echo "📋 Tables principales à explorer :"
echo "  👥 USERS         - Utilisateurs"
echo "  🏃 COURSES       - Courses"
echo "  🎯 PARTICIPATIONS - Inscriptions"
echo "  🏷️  DOSSARDS     - Numéros de course"
echo "  📧 VERIFICATION_TOKENS - Tokens email"
echo ""
echo "💡 Appuyez sur Ctrl+C pour arrêter la console"
echo ""

# Démarrer la console H2
java -cp "$H2_JAR" org.h2.tools.Server -web -browser -webPort 8082 -baseDir "$(pwd)" 