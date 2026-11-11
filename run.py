#!/usr/bin/env python3
"""
AquaSafe - Single Command Cross-Platform Project Launcher
Runs the complete AquaSafe Water-Borne Disease Prediction & Big Data Surveillance System.

Usage:
    python run.py            # Normal 1-command startup (validates environment, builds if needed, starts server & opens browser)
    python run.py --build    # Rebuilds Maven package and runs
    python run.py --spark    # Runs the PySpark big data analytics pipeline & generates Parquet
    python run.py --ml       # Runs Scikit-Learn training, benchmarking & time-series forecasting
    python run.py --data     # Generates 5-year multi-source datasets (2021-2026) for all 38 districts
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
    print("=" * 80)
    print("      AQUASAFE - TAMIL NADU WATER-BORNE DISEASE PREDICTION PLATFORM           ")
    print("    Big Data Analytics & Machine Learning Epidemiological Surveillance        ")
    print("=" * 80)

def check_java():
    print("[*] Checking Java Runtime Environment...")
    try:
        res = subprocess.run(["java", "-version"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if res.returncode != 0:
            raise RuntimeError("Java command failed")
        first_line = (res.stderr or res.stdout).split("\n")[0]
        print(f"[+] Java detected: {first_line}")
        return True
    except Exception:
        print("[ERROR] Java 17+ is required but not found in system PATH.")
        print("Please download and install Java 17+ from: https://adoptium.net/")
        return False

def check_data():
    master_csv = os.path.join(ROOT_DIR, "data", "raw", "districts_master.csv")
    if not os.path.exists(master_csv):
        print("[*] 5-Year Tamil Nadu datasets not found. Generating now for all 38 districts...")
        script = os.path.join(ROOT_DIR, "scripts", "generate_tamilnadu_dataset.py")
        ret = subprocess.run([sys.executable, script], cwd=ROOT_DIR)
        if ret.returncode != 0:
            print("[WARN] Data generation returned non-zero code. Continuing with existing data.")

def check_spark_data():
    parquet_path = os.path.join(ROOT_DIR, "data", "processed", "parquet", "tamilnadu_epidemiological_analytics.parquet")
    if not os.path.exists(parquet_path):
        print("[*] Processed Parquet dataset not detected. Running Apache Spark pipeline...")
        run_spark(exit_after=False)

def check_ml_models():
    metrics_path = os.path.join(ROOT_DIR, "ml", "models", "model_evaluation_metrics.json")
    if not os.path.exists(metrics_path):
        print("[*] Machine Learning models not detected. Running ML training pipeline...")
        run_ml(exit_after=False)

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

def run_spark(exit_after=True):
    banner()
    print("[*] Executing AquaSafe Apache Spark Big Data Analytics Pipeline...")
    spark_script = os.path.join(ROOT_DIR, "spark", "spark_processor.py")
    ret = subprocess.run([sys.executable, spark_script, "local[*]", os.path.join(ROOT_DIR, "data")])
    if exit_after:
        sys.exit(ret.returncode)

def run_ml(exit_after=True):
    banner()
    print("[*] Executing AquaSafe Machine Learning & Forecasting Engine...")
    ml_script = os.path.join(ROOT_DIR, "ml", "ml_engine.py")
    ret = subprocess.run([sys.executable, ml_script], cwd=ROOT_DIR)
    if exit_after:
        sys.exit(ret.returncode)

def run_data():
    banner()
    print("[*] Generating 5-Year Tamil Nadu datasets (2021-2026) for all 38 districts...")
    script = os.path.join(ROOT_DIR, "scripts", "generate_tamilnadu_dataset.py")
    ret = subprocess.run([sys.executable, script], cwd=ROOT_DIR)
    sys.exit(ret.returncode)

def main():
    os.chdir(ROOT_DIR)
    banner()

    args = sys.argv[1:]
    if "--help" in args or "-h" in args:
        print("""AquaSafe Launcher Options:
    python run.py            # Normal 1-command startup (builds if needed, starts server & opens browser)
    python run.py --build    # Rebuilds Maven package and runs server
    python run.py --spark    # Executes Apache Spark / PySpark 4.2.0 ETL & Parquet generation
    python run.py --ml       # Executes Scikit-Learn training, benchmarking & time-series forecasting
    python run.py --data     # Regenerates 5-year multi-source datasets (2021-2026) for all 38 districts
    python run.py --help     # Displays this help message
        """)
        return
    if "--spark" in args:
        run_spark(exit_after=True)
        return

    if "--ml" in args:
        run_ml(exit_after=True)
        return

    if "--data" in args or "--seed" in args:
        run_data()
        return

    if not check_java():
        sys.exit(1)

    check_data()
    check_spark_data()
    check_ml_models()

    if "--build" in args or not os.path.exists(JAR_PATH):
        build_jar()

    # Launch browser thread
    threading.Thread(target=open_browser_when_ready, daemon=True).start()

    print()
    print("=" * 80)
    print(f" Launching AquaSafe Application Server at: {APP_URL}")
    print(" - All 38 Districts of Tamil Nadu Active (2021-2026)")
    print(" - Admin Account:   admin   / admin123")
    print(" - Analyst Account: analyst / user123")
    print(" - Press Ctrl+C in this console to stop the server")
    print("=" * 80)
    print()

    try:
        subprocess.run(["java", "-jar", JAR_PATH])
    except KeyboardInterrupt:
        print("\n[*] AquaSafe Application Server shutdown requested. Goodbye!")

if __name__ == "__main__":
    main()