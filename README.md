# AquaSafe: Water-Borne Disease Prediction Using Big Data Analytics & Machine Learning

AquaSafe is an epidemiological surveillance and predictive healthcare intelligence platform. It integrates environmental water quality sensors, meteorological observation feeds, demographic indicators, and clinical disease reports to predict and prevent outbreaks of water-borne diseases (Cholera, Typhoid, Diarrhea, and Hepatitis) across Tamil Nadu districts.

---

## ⚡ Quick Start (Single Command)

To run the entire AquaSafe application with a single command:

### Windows:
```cmd
.\run.bat
```
*(Or in PowerShell: `.\run.ps1`)*

### Cross-Platform (Windows / Mac / Linux):
```bash
python run.py
```

### Linux / macOS:
```bash
./run.sh
```

### Make:
```bash
make run
```

The system will start up, configure the database, seed demonstration data, validate the Machine Learning model, and automatically open your web browser to:
```
http://localhost:8080
```

---

## 📚 Complete Project Documentation

For full technical specifications, architecture diagrams, mathematical equations, REST APIs, and operation steps, please refer to the dedicated guide:

👉 **[PROJECT_GUIDE.md](PROJECT_GUIDE.md)**  
👉 **[RUN_GUIDE.md](RUN_GUIDE.md)**

---

## 🔑 Default Credentials

- **Administrator**: `admin` / `admin123`
- **Analyst**: `analyst` / `user123`

---

## 🛠️ Technical Stack Summary

- **Backend**: Spring Boot 3.2.5, Java 17 LTS, Spring Data JPA, Spring Security, OpenCSV
- **Machine Learning**: Weka 3.8.6 (Random Forest, 100 Trees, 10-Fold Cross-Validation)
- **Big Data Engine**: Apache Spark 3.x, PySpark, Multi-Threaded Local Dispatcher
- **Database**: Zero-Config In-Memory H2 (MySQL Mode) + Production MySQL 8.x
- **Frontend**: HTML5, Modern CSS3 Glassmorphic Theme, Vanilla JS ES6+, Bootstrap 5.3, Chart.js 4, Leaflet.js
- **Weather API**: Open-Meteo REST API

---

## 📄 License
AquaSafe &copy; 2026. Predictive Public Health Analytics.
