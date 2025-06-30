#!/bin/bash

echo "📦 Téléchargement du driver H2 pour SQLTools..."

# Répertoire pour les drivers
DRIVERS_DIR="$HOME/.sqltools-drivers"
H2_DIR="$DRIVERS_DIR/h2"

# Créer les répertoires
mkdir -p "$H2_DIR"

# URL du driver H2
H2_VERSION="2.1.214"
H2_URL="https://repo1.maven.org/maven2/com/h2database/h2/${H2_VERSION}/h2-${H2_VERSION}.jar"
H2_JAR="$H2_DIR/h2-${H2_VERSION}.jar"

echo "🌐 Téléchargement depuis: $H2_URL"
echo "📁 Destination: $H2_JAR"

# Télécharger le driver
if curl -L -o "$H2_JAR" "$H2_URL"; then
    echo "✅ Driver H2 téléchargé avec succès !"
    echo ""
    echo "📋 Configuration SQLTools :"
    echo "  Driver Path: $H2_JAR"
    echo "  Driver Class: org.h2.Driver"
    echo "  JDBC URL: jdbc:h2:file:$PWD/dbFiles/Runton_color_Prod;AUTO_SERVER=TRUE"
    echo "  Username: Runton"
    echo "  Password: (vide)"
else
    echo "❌ Erreur lors du téléchargement"
    exit 1
fi

# Aussi copier depuis Maven si disponible
MAVEN_H2="$HOME/.m2/repository/com/h2database/h2/${H2_VERSION}/h2-${H2_VERSION}.jar"
if [ -f "$MAVEN_H2" ]; then
    echo "📋 Driver Maven trouvé, copie..."
    cp "$MAVEN_H2" "$H2_JAR"
    echo "✅ Copie depuis Maven réussie !"
fi

echo ""
echo "🚀 Le driver H2 est prêt pour SQLTools !"
echo "📖 Utilisez le chemin: $H2_JAR" 