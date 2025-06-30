#!/bin/bash

# Script pour exécuter des requêtes rapides sur la base Color Run

DB_PATH="./dbFiles/Runton_color_Prod"
H2_JAR="$HOME/.m2/repository/com/h2database/h2/2.1.214/h2-2.1.214.jar"

echo "🔍 REQUÊTES RAPIDES - BASE COLOR RUN"
echo "====================================="

show_menu() {
    echo ""
    echo "Choisissez une action :"
    echo "1. 👥 Voir tous les utilisateurs"
    echo "2. 🏃 Voir toutes les courses"
    echo "3. 🎯 Voir les participations"
    echo "4. 🏷️  Voir les dossards"
    echo "5. 📧 Voir les tokens de vérification"
    echo "6. 📊 Statistiques générales"
    echo "7. 🔧 Requête personnalisée"
    echo "8. 🌐 Ouvrir console web"
    echo "0. ❌ Quitter"
    echo ""
    echo -n "Votre choix [0-8] : "
}

execute_query() {
    local query="$1"
    echo ""
    echo "🔍 Exécution : $query"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    java -cp "$H2_JAR" org.h2.tools.Shell -url "jdbc:h2:$PWD/$DB_PATH" -user "Runton" -sql "$query"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

while true; do
    show_menu
    read choice
    
    case $choice in
        1)
            execute_query "SELECT ID, FIRST_NAME, LAST_NAME, EMAIL, ROLE, ENABLED FROM USERS ORDER BY ID;"
            ;;
        2)
            execute_query "SELECT ID, NAME, DESCRIPTION, CITY, DATE, DISTANCE, MAX_PARTICIPANTS FROM COURSES ORDER BY DATE;"
            ;;
        3)
            execute_query "SELECT p.ID, u.FIRST_NAME, u.LAST_NAME, c.NAME as COURSE, p.REGISTRATION_DATE FROM PARTICIPATIONS p JOIN USERS u ON p.USER_ID = u.ID JOIN COURSES c ON p.COURSE_ID = c.ID ORDER BY p.REGISTRATION_DATE DESC;"
            ;;
        4)
            execute_query "SELECT d.ID, d.NUMBER, c.NAME as COURSE, u.FIRST_NAME, u.LAST_NAME, d.STATUS FROM DOSSARDS d JOIN COURSES c ON d.COURSE_ID = c.ID JOIN USERS u ON d.USER_ID = u.ID ORDER BY d.NUMBER;"
            ;;
        5)
            execute_query "SELECT ID, USER_ID, TOKEN, EXPIRES_AT, USED FROM VERIFICATION_TOKENS ORDER BY EXPIRES_AT DESC;"
            ;;
        6)
            echo ""
            echo "📊 STATISTIQUES GÉNÉRALES"
            echo "========================="
            execute_query "SELECT 'Utilisateurs' as TYPE, COUNT(*) as TOTAL FROM USERS UNION ALL SELECT 'Courses', COUNT(*) FROM COURSES UNION ALL SELECT 'Participations', COUNT(*) FROM PARTICIPATIONS UNION ALL SELECT 'Dossards', COUNT(*) FROM DOSSARDS;"
            ;;
        7)
            echo ""
            echo -n "Entrez votre requête SQL : "
            read custom_query
            if [ ! -z "$custom_query" ]; then
                execute_query "$custom_query"
            fi
            ;;
        8)
            echo ""
            echo "🌐 Ouverture de la console web..."
            ./access-database.sh
            ;;
        0)
            echo "👋 Au revoir !"
            exit 0
            ;;
        *)
            echo "❌ Choix invalide. Veuillez choisir entre 0 et 8."
            ;;
    esac
    
    echo ""
    echo -n "Appuyez sur Entrée pour continuer..."
    read
done 