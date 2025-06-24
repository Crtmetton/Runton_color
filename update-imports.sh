#!/bin/bash

# Trouver tous les fichiers .java
echo "Remplacer les imports jakarta par javax..."
find src/main/java -name "*.java" -type f -exec sed -i '' 's/import jakarta\.servlet/import javax.servlet/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i '' 's/import jakarta\.mail/import javax.mail/g' {} \;

echo "Mise à jour terminée" 