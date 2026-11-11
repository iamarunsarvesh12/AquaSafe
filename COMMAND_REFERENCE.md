# AquaSafe: Command Reference Manual

This manual provides a quick-lookup reference for all operational, development, Big Data, and Machine Learning commands across Windows PowerShell, CMD, Linux, and macOS.

---

## 1. Primary One-Command Launchers

| Task | Windows PowerShell | Windows CMD | Linux / macOS | Python (Cross-Platform) |
|---|---|---|---|---|
| **Start Full Platform** | `.\run.ps1` | `run.bat` | `./run.sh` | `python run.py` |
| **Rebuild & Start** | `.\run.ps1` | `run.bat` | `./run.sh` | `python run.py --build` |
| **Run Big Data Spark Job** | `python run.py --spark` | `python run.py --spark` | `python3 run.py --spark` | `python run.py --spark` |
| **Run ML Training Engine** | `python run.py --ml` | `python run.py --ml` | `python3 run.py --ml` | `python run.py --ml` |
| **Regenerate Datasets** | `python run.py --data` | `python run.py --data` | `python3 run.py --data` | `python run.py --data` |

---

## 2. Data Engineering & Spark Commands

### Generate 5-Year Datasets (2021–2026 for All 38 Districts):
```powershell
# PowerShell / CMD
python scripts/generate_tamilnadu_dataset.py

# Linux / macOS
python3 scripts/generate_tamilnadu_dataset.py
```

### Run PySpark 4.2.0 Distributed Processing Pipeline:
```powershell
# Run with local Spark master using all cores
python spark/spark_processor.py local[*] data/

# Run against a standalone Spark cluster
python spark/spark_processor.py spark://master:7077 data/
```

### Inspect Generated Parquet Dataset:
```powershell
# PowerShell
Get-ChildItem -Path data/processed/parquet/tamilnadu_epidemiological_analytics.parquet -Recurse

# Linux / macOS
ls -lah data/processed/parquet/tamilnadu_epidemiological_analytics.parquet
```

---

## 3. Machine Learning & Forecasting Commands

### Run Scikit-Learn Training & Model Evaluation:
```powershell
python ml/ml_engine.py
```

### Inspect Model Evaluation Metrics JSON:
```powershell
# PowerShell
Get-Content ml/models/model_evaluation_metrics.json | ConvertFrom-Json | Format-List

# Linux / macOS
cat ml/models/model_evaluation_metrics.json | jq .
```

### Inspect Prospective Time-Series Forecasts:
```powershell
# PowerShell
Get-Content ml/models/disease_forecasts.json | ConvertFrom-Json | Select-Object -First 10

# Linux / macOS
head -n 25 ml/models/disease_forecasts.json
```

---

## 4. Maven Backend Build & Testing Commands

### Run Backend Unit & Integration Tests:
```powershell
# Run tests
mvn test -f backend/pom.xml

# Run with full debug output
mvn test -X -f backend/pom.xml
```

### Build & Package Standalone Spring Boot JAR:
```powershell
# Fast build (skipping tests)
mvn clean package -DskipTests -f backend/pom.xml

# Build with tests executed
mvn clean package -f backend/pom.xml
```

### Run Standalone Packaged JAR Directly:
```powershell
java -jar backend/target/aquasafe-backend-1.0.0-SNAPSHOT.jar
```

---

## 5. Database & Schema Commands

### Initialize MySQL Database:
```powershell
# Windows / Linux
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS aquasafe_db;"
mysql -u root -p aquasafe_db < sql/schema.sql
```

### Connect to Embedded H2 Web Console:
When running with H2 database fallback:
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:aquasafedb;DB_CLOSE_DELAY=-1;MODE=MySQL`
- **Username:** `sa`
- **Password:** `[empty]`

