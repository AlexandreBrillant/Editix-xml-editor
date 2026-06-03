#!/usr/bin/env python3
"""
Script pour générer run.sh et run.bat avec des chemins RELATIFS à partir du .classpath d'Eclipse.
Utilisation :
    python3 generate_run_scripts.py [chemin/vers/.classpath] [main_class] [dossier_sortie]

Exemple :
    python3 generate_run_scripts.py ./mon_projet/.classpath com.monapp.Main ./mon_projet
"""

import os
import sys
import xml.etree.ElementTree as ET

def resolve_classpath_entries(classpath_file, project_dir):
    """Extrait et résout les entrées de type 'lib' du fichier .classpath (chemins absolus)."""
    tree = ET.parse(classpath_file)
    root = tree.getroot()
    classpath_entries = []

    for entry in root.findall('classpathentry'):
        kind = entry.get('kind')
        path = entry.get('path')

        if kind != 'lib':
            continue

        # Ignore les variables Eclipse non résolues
        if path and any(var in path for var in ['$', 'M2_REPO', 'JRE_CONTAINER', 'CLASSPATH_VAR']):
            print(f"⚠️  Ignore (variable Eclipse) : {path}")
            continue

        if path:
            resolved_path = os.path.abspath(os.path.join(project_dir, path))
            if os.path.isfile(resolved_path):
                classpath_entries.append(resolved_path)
            else:
                print(f"⚠️  Fichier introuvable : {resolved_path}")

    return classpath_entries

def make_relative_paths(absolute_paths, output_dir):
    """Convertit une liste de chemins absolus en chemins relatifs par rapport à output_dir."""
    relative_paths = []
    for abs_path in absolute_paths:
        rel_path = os.path.relpath(abs_path, output_dir)
        # Remplace les séparateurs Windows par des / pour la compatibilité
        rel_path = rel_path.replace(os.sep, '/')
        relative_paths.append(rel_path)
    return relative_paths

def generate_shell_script(relative_paths, main_class, output_file="run.sh"):
    """Génère run.sh avec des chemins relatifs."""
    classpath_str = ":".join(f"./{p}" if not p.startswith('./') else p for p in relative_paths)
    script_content = f"""#!/bin/bash

# Script généré à partir du .classpath d'Eclipse (chemins relatifs)
# Classe principale : {main_class}

CLASSPATH="{classpath_str}"

# Ajoute le répertoire courant au classpath
CLASSPATH=".:$CLASSPATH"

echo "Exécution avec classpath : $CLASSPATH"
java -cp "$CLASSPATH" {main_class}
"""
    with open(output_file, 'w') as f:
        f.write(script_content)

    if os.name != 'nt':
        os.chmod(output_file, 0o755)
    print(f"✅ Généré : {output_file}")

def generate_batch_script(relative_paths, main_class, output_file="run.bat"):
    """Génère run.bat avec des chemins relatifs (séparateur ;)."""
    classpath_str = ";".join(f".\\{p}" if not p.startswith('./') and not p.startswith('.\\') else p.replace('/', '\\\\') for p in relative_paths)
    script_content = f"""@echo off

REM Script généré à partir du .classpath d'Eclipse (chemins relatifs)
REM Classe principale : {main_class}

set CLASSPATH={classpath_str}

REM Ajoute le répertoire courant au classpath
set CLASSPATH=.;%CLASSPATH%

echo Exécution avec classpath : %CLASSPATH%
java -cp "%CLASSPATH%" {main_class}
pause
"""
    with open(output_file, 'w') as f:
        f.write(script_content)
    print(f"✅ Généré : {output_file}")

def main():
    if len(sys.argv) < 3:
        print("Usage: python3 generate_run_scripts.py <.classpath> <main_class> [dossier_sortie]")
        print("Exemple: python3 generate_run_scripts.py ./mon_projet/.classpath com.app.Main ./mon_projet")
        sys.exit(1)

    classpath_file = os.path.abspath(sys.argv[1])
    main_class = sys.argv[2]
    output_dir = os.path.abspath(sys.argv[3]) if len(sys.argv) > 3 else os.getcwd()

    if not os.path.isfile(classpath_file):
        print(f"❌ Fichier introuvable : {classpath_file}")
        sys.exit(1)

    project_dir = os.path.dirname(classpath_file)
    absolute_paths = resolve_classpath_entries(classpath_file, project_dir)

    if not absolute_paths:
        print("⚠️  Aucune entrée 'lib' valide trouvée.")
        sys.exit(1)

    print(f"📂 Dossier de sortie : {output_dir}")
    relative_paths = make_relative_paths(absolute_paths, output_dir)

    print("📦 Chemins relatifs générés :")
    for p in relative_paths:
        print(f"   - {p}")

    os.makedirs(output_dir, exist_ok=True)
    os.chdir(output_dir)

    generate_shell_script(relative_paths, main_class)
    generate_batch_script(relative_paths, main_class)

    print("\n🎉 Scripts générés avec des chemins RELATIFS !")

if __name__ == "__main__":
    main()
