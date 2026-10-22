#!/usr/bin/env bash
# AquaSafe - Single Command Shell Launcher for Linux / macOS / Git Bash
# Usage: ./run.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=============================================================================="
echo "                AQUASAFE - WATER-BORNE DISEASE INTELLIGENCE                   "
echo "           Big Data Analytics & Machine Learning Surveillance System          "
echo "=============================================================================="
echo ""

# 1. Check Java
if ! command -v java &>/dev/null; then
    echo "[ERROR] Java 17+ is required but not found in PATH."
    echo "Please install Java 17+ from: https://adoptium.net/"
    exit 1
fi
echo "[+] Java detected: $(java -version 2>&1 | head -n 1)"

# 2. Check / Build JAR
JAR_PATH="$SCRIPT_DIR/backend/target/aquasafe-backend-1.0.0-SNAPSHOT.jar"
if [ ! -f "$JAR_PATH" ]; then
    echo "[*] Compiled JAR not found. Building with Maven..."
    (cd "$SCRIPT_DIR/backend" && mvn clean package -DskipTests)
    if [ ! -f "$JAR_PATH" ]; then
        echo "[ERROR] Maven build failed."
        exit 1
    fi
    echo "[+] Build completed successfully."
fi

# 3. Background browser opener
(
    attempts=0
    while [ $attempts -lt 40 ]; do
        sleep 2
        if curl -s -f http://localhost:8080/api/system/health | grep -q '"status":"UP"'; then
            echo ""
            echo "[+] AquaSafe Server is UP! Opening browser..."
            if command -v xdg-open &>/dev/null; then
                xdg-open "http://localhost:8080" &>/dev/null &
            elif command -v open &>/dev/null; then
                open "http://localhost:8080" &>/dev/null &
            elif command -v start &>/dev/null; then
                start "http://localhost:8080" &>/dev/null &
            fi
            break
        fi
        attempts=$((attempts + 1))
    done
) &

# 4. Start Server
echo ""
echo "=============================================================================="
echo " Launching AquaSafe Application Server at: http://localhost:8080"
echo " - Admin Account:   admin   / admin123"
echo " - Analyst Account: analyst / user123"
echo " - Press Ctrl+C in this console to stop the server"
echo "=============================================================================="
echo ""

exec java -jar "$JAR_PATH"
