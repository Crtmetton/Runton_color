#!/bin/bash

echo "=== Accès à la base de données H2 Color Run ==="
echo ""

# Chemin vers la base
DB_PATH="./dbFiles/Runton_color_Prod"
H2_JAR="$HOME/.m2/repository/com/h2database/h2/2.1.214/h2-2.1.214.jar"

echo "🗄️  Base de données : $DB_PATH"
echo "👤 Utilisateur : Runton"
echo "🔑 Mot de passe : (vide)"
echo ""

# Vérifier si H2 est téléchargé
if [ ! -f "$H2_JAR" ]; then
    echo "⚠️  H2 JAR non trouvé. Téléchargement via Maven..."
    mvn dependency:get -Dartifact=com.h2database:h2:2.1.214
fi

echo "🚀 Démarrage de la console H2..."
echo "🌐 Console disponible sur : http://localhost:8082"
echo ""
echo "Configuration de connexion :"
echo "  JDBC URL: jdbc:h2:$PWD/dbFiles/Runton_color_Prod"
echo "  User Name: Runton"
echo "  Password: (laissez vide)"
echo ""

# Démarrer la console H2
java -cp "$H2_JAR" org.h2.tools.Server -web -browser -webPort 8082 