#!/bin/bash

# Vérifie qu'une classe est fournie en argument
if [ $# -eq 0 ]; then
    echo "Usage: $0 <nom_de_la_classe>"
    echo "Exemple: $0 com.example.MaClasse"
    exit 1
fi

CLASS="$1"
# Convertit le nom de la classe en chemin de fichier .class (remplace . par / et ajoute .class)
CLASS_PATH="${CLASS//./\/}.class"

echo "Recherche de la classe : $CLASS"
echo "Chemin attendu dans le JAR : $CLASS_PATH"
echo "---"

# Trouve tous les fichiers .jar dans le répertoire courant et ses sous-répertoires
find . -name "*.jar" -type f | while read -r jar; do
    # Vérifie si le fichier .class existe dans le JAR
    if jar tf "$jar" | grep -q "^$CLASS_PATH$"; then
        echo "✅ Trouvé dans : $jar"
    fi
done

echo "---"
echo "Recherche terminée."
