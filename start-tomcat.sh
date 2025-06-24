#!/bin/bash

echo "Arrêt de tous les processus Tomcat en cours..."
pkill -f tomcat || true

echo "Nettoyage du cache Maven et recompilation..."
rm -rf target
rm -rf ~/.m2/repository/fr/esgi/runton-color

echo "Réinitialisation de la base de données..."
./update-database.sh
./insert-test-data.sh

echo "Compilation du projet..."
mvn clean package

# Vérifier si le port 8090 est déjà utilisé
if lsof -Pi :8090 -sTCP:LISTEN -t >/dev/null ; then
    echo "Le port 8090 est déjà utilisé. Arrêt du processus..."
    kill $(lsof -Pi :8090 -sTCP:LISTEN -t)
    sleep 2
fi

echo "Démarrage de Tomcat sur le port 8090..."
echo "L'application sera accessible à l'adresse: http://localhost:8090/runton-color"
mvn tomcat7:run -Dmaven.tomcat.port=8090 