#!/bin/bash

# Définition des variables
DB_URL="jdbc:h2:file:./dbFiles/Runton_color_Prod"
DB_USER="Runton"
DB_PASSWORD=""
SQL_SCRIPT="db/sqlScriptDropAndCreateUpdated.sql"
H2_JAR_PATH="target/dependency/h2-2.1.214.jar"

# Vérifier si le jar H2 existe
if [ ! -f "$H2_JAR_PATH" ]; then
    echo "Téléchargement de la bibliothèque H2..."
    mkdir -p target/dependency
    curl -L https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar -o "$H2_JAR_PATH"
fi

# Exécuter le script SQL
echo "Mise à jour de la base de données avec le script $SQL_SCRIPT..."
java -cp "$H2_JAR_PATH" org.h2.tools.RunScript -url "$DB_URL" -user "$DB_USER" -password "$DB_PASSWORD" -script "$SQL_SCRIPT" -showResults

echo "Base de données mise à jour avec succès !" 