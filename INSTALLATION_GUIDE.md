# AquaSafe: Installation & Environment Setup Guide

This guide provides step-by-step instructions for installing and running **AquaSafe** across Windows, Linux, and macOS.

---

## 1. System Requirements & Prerequisites

| Component | Minimum Requirement | Recommended | Verification Command |
|---|---|---|---|
| **Operating System** | Windows 10/11, Ubuntu 20.04+, macOS 12+ | Windows 11 / Ubuntu 22.04 LTS | `[System Info]` |
| **Java JDK** | OpenJDK 17 LTS | Eclipse Temurin 17 LTS | `java -version` |
| **Python** | Python 3.10+ | Python 3.11.x | `python --version` |
| **Apache Maven** | Maven 3.8.0+ | Maven 3.9+ | `mvn -v` |
| **Database** | Embedded H2 (Zero-Install fallback included) | MySQL 8.0+ (Production) | `mysql --version` |
| **Memory (RAM)** | 4 GB | 8 GB or 16 GB | - |
| **Storage** | 2 GB free disk space | 5 GB SSD free space | - |

---

## 2. Java 17 Installation

### Windows:
1. Download Eclipse Temurin 17 (LTS) MSI installer from: [https://adoptium.net/](https://adoptium.net/)
2. Run installer and ensure **"Set JAVA_HOME variable"** and **"Add to PATH"** are checked.
3. Verify in PowerShell:
   ```powershell
   java -version
   ```

### Linux (Ubuntu / Debian):
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven
java -version
```

### macOS (Homebrew):
```bash
brew install openjdk@17 maven
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
java -version
```

---

## 3. Python 3.11 & Required Packages

AquaSafe requires Python 3.10+ for PySpark 4.2.0, Scikit-Learn, and PyArrow.

### Install Python Packages:
From the project root:
```bash
pip install -r requirements.txt
```

If `requirements.txt` is not present, install core dependencies directly:
```bash
pip install pyspark==4.2.0 pyarrow pandas scikit-learn joblib requests
```

---

## 4. Database Setup (Optional Production MySQL 8.x)

> [!NOTE]
> AquaSafe includes a built-in **H2 Database engine running in MySQL compatibility mode**. If MySQL is not running on port 3306, AquaSafe boots automatically using in-memory H2 with zero manual setup.

### To use native MySQL 8.x (Optional):
1. Start MySQL Server:
   ```sql
   CREATE DATABASE IF NOT EXISTS aquasafe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Initialize database schema:
   ```bash
   mysql -u root -p aquasafe_db < sql/schema.sql
   ```
3. Update `backend/src/main/resources/application.properties` if using custom credentials.

---

## 5. Building & Verifying Backend

From the project root:
```bash
cd backend
mvn clean package -DskipTests
cd ..
```
Verify that `backend/target/aquasafe-backend-1.0.0-SNAPSHOT.jar` is generated.

---

## 6. Running AquaSafe

### Single-Command Cross-Platform Launcher:
```bash
# Recommended cross-platform launcher (Windows, Linux, macOS)
python run.py

# Or on Windows PowerShell:
.\run.ps1

# Or on Windows Command Prompt:
run.bat

# Or on Linux / macOS:
chmod +x run.sh
./run.sh
```

The launcher will:
1. Verify Java 17 and Python 3.11.
2. Verify multi-source 5-year datasets for all 38 districts.
3. Verify PySpark processing and Parquet data.
4. Verify Machine Learning models.
5. Launch Spring Boot on `http://localhost:8080`.
6. Automatically open your default web browser to the dashboard.

### Default Login Accounts:
- **System Administrator:** `admin` / `admin123`
- **Public Health Analyst:** `analyst` / `user123`
- **District Health Officer:** `dho_chennai` / `officer123`

