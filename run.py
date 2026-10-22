#!/usr/bin/env python3
"""
AquaSafe - Single Command Cross-Platform Project Launcher
Runs the complete AquaSafe Water-Borne Disease Prediction & Big Data Surveillance System.

Usage:
    python run.py            # Normal 1-command startup (builds if needed, starts server & opens browser)
    python run.py --build    # Rebuilds Maven package and runs
    python run.py --spark    # Runs the PySpark big data analytics job
    python run.py --seed     # Re-generates demo datasets
"""

import os
import sys
import time
import subprocess
import threading
import urllib.request
import json
import webbrowser

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
JAR_PATH = os.path.join(ROOT_DIR, "backend", "target", "aquasafe-backend-1.0.0-SNAPSHOT.jar")
HEALTH_URL = "http://localhost:8080/api/system/health"
APP_URL = "http://localhost:8080"

def banner():
    print("=" * 78)
    print("                AQUASAFE - WATER-BORNE DISEASE INTELLIGENCE                   ")
    print("           Big Data Analytics & Machine Learning Surveillance System          ")
    print("=" * 78)

def check_java():
    print("[*] Checking Java Runtime Environment...")
    try:
        res = subprocess.run(["java", "-version"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if res.returncode != 0:
            raise RuntimeError("Java command failed")
        first_line = (res.stderr or res.stdout).split("\n")[0]
        print(f"[+] Java detected: {first_line}")
        return True
    except Exception as e:
        print(f"[ERROR] Java 17+ is required but not found in system PATH.")
        print("Please download and install Java 17+ from: https://adoptium.net/")
        return False

def build_jar():
    print("[*] Building AquaSafe backend with Maven...")
    mvn_cmd = "mvn.cmd" if sys.platform == "win32" else "mvn"
    backend_dir = os.path.join(ROOT_DIR, "backend")
    ret = subprocess.run([mvn_cmd, "clean", "package", "-DskipTests"], cwd=backend_dir)
    if ret.returncode != 0:
        print("[ERROR] Maven build failed. Check output above.")
        sys.exit(1)
    print("[+] Maven package completed successfully.")

def open_browser_when_ready():
    attempts = 0
    max_attempts = 45
    print("[*] Initializing automated browser launcher helper...")
    while attempts < max_attempts:
        time.sleep(2)
        try:
            req = urllib.request.Request(HEALTH_URL, headers={"User-Agent": "AquaSafe-Launcher"})
            with urllib.request.urlopen(req, timeout=2) as response:
                if response.status == 200:
                    data = json.loads(response.read().decode())
                    if data.get("status") == "UP":
                        print(f"\n[+] AquaSafe Server is UP & Healthy! Launching browser at: {APP_URL}")
                        webbrowser.open(APP_URL)
                        return
        except Exception:
            pass
        attempts += 1

def run_spark():
    banner()
    print("[*] Executing AquaSafe Apache Spark Big Data Analytics Pipeline...")
    spark_script = os.path.join(ROOT_DIR, "spark", "spark_processor.py")
    ret = subprocess.run([sys.executable, spark_script, "local[*]", os.path.join(ROOT_DIR, "data")])
    sys.exit(ret.returncode)

def run_seed():
    banner()
    print("[*] Regenerating synthetic surveillance data & ARFF training sets...")
    seed_script = os.path.join(ROOT_DIR, "generate_demo_data.py")
    ret = subprocess.run([sys.executable, seed_script], cwd=ROOT_DIR)
    sys.exit(ret.returncode)

def main():
    os.chdir(ROOT_DIR)
    banner()

    args = sys.argv[1:]
    if "--spark" in args:
        run_spark()
        return

    if "--seed" in args:
        run_seed()
        return

    if not check_java():
        sys.exit(1)

    if "--build" in args or not os.path.exists(JAR_PATH):
        build_jar()

    # Launch browser thread
    threading.Thread(target=open_browser_when_ready, daemon=True).start()

    print()
    print("=" * 78)
    print(f" Launching AquaSafe Application Server at: {APP_URL}")
    print(" - Admin Account:   admin   / admin123")
    print(" - Analyst Account: analyst / user123")
    print(" - Press Ctrl+C in this console to stop the server")
    print("=" * 78)
    print()

    try:
        subprocess.run(["java", "-jar", JAR_PATH])
    except KeyboardInterrupt:
        print("\n[*] AquaSafe Application Server shutdown requested. Goodbye!")

if __name__ == "__main__":
    main()
