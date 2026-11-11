# AquaSafe: Complete REST API Documentation

This document provides the reference guide for all RESTful API endpoints exposed by the **AquaSafe** Spring Boot backend running on `http://localhost:8080/api`.

---

## 1. Global Response & Authentication Standards

- **Base Path:** `/api`
- **Data Exchange Format:** `application/json;charset=UTF-8`
- **Standard HTTP Status Codes:**
  - `200 OK`: Request succeeded and payload returned.
  - `201 Created`: Resource successfully created.
  - `400 Bad Request`: Validation failure or malformed payload.
  - `404 Not Found`: Requested district, model, or resource does not exist.
  - `500 Internal Server Error`: Server exception with structured JSON message.

---

## 2. API Endpoints Reference

### A. Districts Master APIs (`/api/districts`)

#### 1. Get All 38 Districts
- **Method:** `GET /api/districts`
- **Description:** Returns the complete master registry of all 38 districts of Tamil Nadu.
- **Response `200 OK`:**
```json
[
  {
    "id": 1,
    "districtName": "Ariyalur",
    "districtCode": "TN-01",
    "latitude": 11.1401,
    "longitude": 79.0786,
    "areaSqKm": 1949.3,
    "population2011": 754894,
    "growthRatePct": 1.12,
    "population2021": 844000,
    "population2025": 882000,
    "population2026": 891800,
    "urbanPopulationPct": 11.1,
    "ruralPopulationPct": 88.9,
    "sanitationCoverage": 88.4
  }
]
```

#### 2. Get District By Name
- **Method:** `GET /api/districts/{name}`
- **Parameters:** `name` (path, string, e.g. `Chennai`)
- **Response `200 OK`:** Single district JSON object.

#### 3. Get Districts Summary Statistics
- **Method:** `GET /api/districts/summary`
- **Response `200 OK`:** Total districts count (38), statewide projected population, average sanitation coverage.

---

### B. Water Quality & Environmental Standards (`/api/water-quality`)

#### 1. Get Water Quality Analytics
- **Method:** `GET /api/water-quality/analytics`
- **Response `200 OK`:**
```json
{
  "averagePh": 7.18,
  "averageTurbidity": 6.85,
  "averageTds": 485.2,
  "averageDissolvedOxygen": 5.42,
  "districtAnalytics": [
    {
      "district": "Ariyalur",
      "avgPh": 7.24,
      "avgTurbidity": 5.12,
      "avgTds": 462.1,
      "avgDissolvedOxygen": 5.80,
      "calculatedWqi": 48.2,
      "wqiStatus": "EXCELLENT"
    }
  ]
}
```

#### 2. Get Water Quality Threshold Violations
- **Method:** `GET /api/water-quality/violations`
- **Description:** Identifies observations violating Bureau of Indian Standards (BIS 10500:2012) drinking water limits.
- **Response `200 OK`:** Array of violation objects with parameter, observed value, permissible standard, and severity (`CRITICAL`, `WARNING`).

---

### C. Weather & Monsoon Analytics (`/api/weather`)

#### 1. Get Weather Analytics
- **Method:** `GET /api/weather/analytics`
- **Response `200 OK`:** Statewide temperature means, average precipitation, 7-day and 30-day moving averages.

#### 2. Get Monsoon Season Analysis
- **Method:** `GET /api/weather/monsoon`
- **Response `200 OK`:**
```json
{
  "southwestMonsoon": {
    "period": "June - September",
    "totalRainfallMm": 340.5,
    "activeDistricts": ["Nilgiris", "Coimbatore", "Kanyakumari"]
  },
  "northeastMonsoon": {
    "period": "October - December",
    "totalRainfallMm": 480.2,
    "activeDistricts": ["Chennai", "Cuddalore", "Nagapattinam", "Thanjavur"]
  }
}
```

---

### D. Disease Surveillance & Incidence (`/api/diseases`)

#### 1. Get Disease Analytics Summary
- **Method:** `GET /api/diseases/analytics`
- **Response `200 OK`:**
```json
{
  "totalRecords": 2698,
  "totalCholeraCases": 412,
  "totalTyphoidCases": 1285,
  "totalDiarrheaCases": 8420,
  "totalHepatitisCases": 395,
  "monthlyTrends": [
    { "month": "2024-06", "diseaseName": "Cholera", "cases": 24 }
  ]
}
```

#### 2. Get District Cumulative Incidence Rates
- **Method:** `GET /api/diseases/incidence`
- **Response `200 OK`:**
```json
[
  {
    "district": "Ariyalur",
    "population": 882000,
    "choleraCases": 12,
    "typhoidCases": 45,
    "diarrheaCases": 310,
    "hepatitisCases": 14,
    "totalCases": 381,
    "incidenceRate": 43.19
  }
]
```

---

### E. Prospective Disease Forecasting (`/api/forecasts`)

#### 1. Get Disease Outbreak Forecasts
- **Method:** `GET /api/forecasts`
- **Optional Query Parameter:** `district` (e.g. `?district=Chennai`)
- **Response `200 OK`:**
```json
[
  {
    "district": "Ariyalur",
    "forecastDate": "2026-11-18",
    "horizonDays": 7,
    "diseaseName": "Total Water-Borne Disease",
    "forecastedCases": 17,
    "lowerConfidence": 13,
    "upperConfidence": 25,
    "modelUsed": "Exponential Trend Smoothing",
    "mae": 18.27,
    "rmse": 18.27
  }
]
```

#### 2. Get Forecast Evaluation Error Metrics
- **Method:** `GET /api/forecasts/metrics`
- **Response `200 OK`:**
```json
{
  "meanAbsoluteError": 34.55,
  "rootMeanSquaredError": 49.33,
  "mape": "14.8%",
  "horizons": ["7-Day Ahead", "30-Day Ahead"],
  "totalForecasts": 76
}
```

---

### F. Machine Learning Model APIs (`/api/model` & `/api/predictions`)

#### 1. Get Model Evaluation Metrics
- **Method:** `GET /api/model/evaluation`
- **Response `200 OK`:** Accuracy, precision, recall, F1-score, confusion matrix, 10-fold CV log.

#### 2. Get Algorithm Benchmark Comparison
- **Method:** `GET /api/model/comparison`
- **Response `200 OK`:** Benchmark metrics comparing Random Forest, Gradient Boosting, Extra Trees, and disease-specific model accuracies.

#### 3. Trigger Model Retraining
- **Method:** `POST /api/model/train`
- **Response `200 OK`:** Retraining completion status and updated evaluation metrics.

#### 4. Execute Risk Prediction
- **Method:** `POST /api/predictions/predict`
- **Request Body:**
```json
{
  "district": "Cuddalore",
  "ph": 7.8,
  "turbidity": 12.5,
  "tds": 650.0,
  "dissolvedOxygen": 4.1,
  "rainfall": 145.0,
  "temperature": 32.5,
  "humidity": 85.0,
  "populationDensity": 730.0,
  "sanitationCoverage": 74.0
}
```
- **Response `200 OK`:**
```json
{
  "district": "Cuddalore",
  "riskScore": 84.5,
  "riskLevel": "HIGH",
  "choleraRisk": 78.4,
  "typhoidRisk": 62.1,
  "diarrheaRisk": 88.5,
  "hepatitisRisk": 54.0,
  "modelVersion": "RandomForest-v1.0",
  "advisoryMessage": "Urgent Public Health Advisory: High water-borne disease risk detected. Advise immediate chlorination booster and drinking water boiling warnings.",
  "predictionTime": "2026-11-11T12:00:00"
}
```

---

### G. Big Data Spark Processing APIs (`/api/system/spark`)

#### 1. Trigger Spark Distributed Job
- **Method:** `POST /api/system/spark/process`
- **Response `200 OK`:**
```json
{
  "status": "SUCCESS",
  "jobName": "PySpark_TamilNadu_ETL_Analytics",
  "recordsProcessed": 2698,
  "durationMs": 6840,
  "partitions": 10,
  "outputPath": "data/processed/parquet/tamilnadu_epidemiological_analytics.parquet"
}
```

#### 2. Get Spark Job History
- **Method:** `GET /api/system/spark/history`
- **Response `200 OK`:** Array of historical Spark job execution records.

#### 3. Get Latest Spark Report
- **Method:** `GET /api/system/spark/report`
- **Response `200 OK`:** Detailed telemetry report from `spark_execution_report.json`.

---

### H. Data Management & Provenance APIs (`/api/datasets`)

#### 1. Get Dataset Sources Registry
- **Method:** `GET /api/datasets/sources`
- **Response `200 OK`:** Array of provenance sources (CGWB, IMD, Census, IDSP).

#### 2. Get Data Quality Reports
- **Method:** `GET /api/datasets/quality`
- **Response `200 OK`:** Completeness, duplicate, and outlier metrics.

#### 3. Upload Custom CSV Dataset
- **Method:** `POST /api/datasets/upload`
- **Body:** `multipart/form-data` with `file` (CSV) and `datasetType` (string).
- **Response `200 OK`:** Ingestion diagnostics with valid/invalid record counts and sample preview rows.

---

### I. User Management APIs (`/api/users`)

#### 1. List Users
- **Method:** `GET /api/users`
- **Response `200 OK`:** List of authorized user accounts.

#### 2. Create User
- **Method:** `POST /api/users`
- **Request Body:** `{ "fullName": "...", "username": "...", "email": "...", "password": "...", "department": "...", "role": "..." }`
- **Response `201 Created`:** Created user entity.

#### 3. Delete User
- **Method:** `DELETE /api/users/{id}`
- **Response `200 OK`:** Confirmation message.

