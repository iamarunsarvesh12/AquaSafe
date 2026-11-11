# AquaSafe: Data Sources & Provenance Specification

This document details the official sources, methodology, access constraints, and provenance classification for all data utilized within the **AquaSafe** platform covering all 38 districts of Tamil Nadu for the period 2021 to 2026.

---

## 1. Data Governance & Provenance Principles

AquaSafe adheres to rigorous open-government data standards and epidemiological research ethics:
1. **No Data Fabrication:** Official source availability is explicitly recorded. Where ground station coverage is intermittent or laboratory measurements are periodic rather than continuous, missingness is recorded via explicit status flags.
2. **Provenance Traceability:** Every imported record maps directly to an entry in `dataset_sources` containing the source URL, collection methodology, and audit classification.
3. **Data Classification Taxonomy:**
   - **`ACTUAL_HISTORICAL`**: Directly derived from published government bulletins, open portal archives, or meteorological stations.
   - **`ESTIMATED`**: Derived from mathematically sound, peer-reviewed estimation models (e.g. population CAGR extrapolations between Census intervals).
   - **`SYNTHETIC`**: Benchmarking scenarios or test sets clearly marked as synthetic to prevent contamination of official surveillance registries.

---

## 2. Multi-Source Dataset Providers & Methodologies

### A. Water Quality Data

- **Primary Agency:** Central Ground Water Board (CGWB), Department of Water Resources, Ministry of Jal Shakti, Government of India.
- **Official Portal:** [https://cgwb.gov.in/](https://cgwb.gov.in/) & National Water Informatics Centre (NWIC) [https://indiawris.gov.in/](https://indiawris.gov.in/)
- **Parameters Monitored:** pH, Electrical Conductivity, Total Dissolved Solids, Turbidity, Dissolved Oxygen, Chloride, Nitrate, Fluoride.
- **Monitoring Architecture & Protocol:**
  - Water quality data in historical government portals is obtained primarily through **quarterly or pre/post-monsoon groundwater laboratory sampling** from national hydrograph stations, rather than continuous high-frequency digital sensors.
  - AquaSafe explicitly labels these records as `station_type: GROUNDWATER_LAB`.
  - Parameters like turbidity and dissolved oxygen are often measured in surface water monitoring rather than deep borewells. When absent from deep aquifer lab sheets, the field is flagged in `missing_parameters` rather than fabricated with artificial noise.
- **Permissible Drinking Water Reference Standards:** Bureau of Indian Standards (BIS 10500:2012) Drinking Water Specification.

---

### B. Meteorological & Weather Data

- **Primary Agency:** India Meteorological Department (IMD), Ministry of Earth Sciences, Government of India.
- **Official Portals:** [https://mausam.imd.gov.in/](https://mausam.imd.gov.in/) & IMD Gridded Weather Portal [https://imdpune.gov.in/](https://imdpune.gov.in/)
- **Complementary Historical Weather API:** Open-Meteo Historical Weather Archive [https://open-meteo.com/en/docs/historical-weather-api](https://open-meteo.com/en/docs/historical-weather-api)
- **Parameters Monitored:** Daily Minimum Temperature ($T_{min}$), Maximum Temperature ($T_{max}$), Average Temperature, 24-Hour Accumulated Rainfall (mm), Relative Humidity (%), Wind Speed (km/h).
- **Attribution & Timestamps:**
  - Observations use UTC and Indian Standard Time (IST) timestamps.
  - Historical weather observations are never combined with speculative long-range forecast values in the historical training set.
  - IMD gridded products (0.25° × 0.25° resolution) are mapped to district centroid coordinates using nearest-neighbor geospatial assignment.

---

### C. Demographic & Population Data

- **Primary Agency:** Office of the Registrar General & Census Commissioner, Ministry of Home Affairs, Government of India.
- **Official Portal:** [https://censusindia.gov.in/](https://censusindia.gov.in/)
- **Sanitation Coverage Reference:** Ministry of Jal Shakti, Swachh Bharat Mission (Grameen) Dashboard [https://sbm.gov.in/](https://sbm.gov.in/)
- **Estimation Methodology (Census Inter-Val Projection):**
  - The last official decennial Census of India was conducted in **2011**. The 2021 decennial census was postponed.
  - To prevent fabricated annual headcount observations, AquaSafe uses the official **Compound Annual Growth Rate (CAGR)** formula recommended by the National Commission on Population:

$$P_t = P_0 \times (1 + r)^t$$

Where:
- $P_0$ is the official 2011 Census baseline population for the district.
- $r$ is the verified district-specific annualized compound growth rate (ranging between 0.8% and 1.4% across Tamil Nadu districts).
- $t$ is the elapsed time in years ($t = \text{Target Year} - 2011$).
- **Classification:** Labeled as `ESTIMATED` with documented baseline attribution.

---

### D. Epidemiological & Disease Surveillance Data

- **Primary Agency:** Integrated Disease Surveillance Programme (IDSP) & Integrated Health Information Platform (IHIP), Directorate of Public Health and Preventive Medicine, Health and Family Welfare Department, Government of Tamil Nadu.
- **Official Portals:** [https://idsp.nic.in/](https://idsp.nic.in/) & [https://ihip.nhp.gov.in/](https://ihip.nhp.gov.in/)
- **Diseases Monitored:**
  1. **Cholera** (*Vibrio cholerae*)
  2. **Typhoid** (*Salmonella enterica* serovar Typhi)
  3. **Acute Diarrhoeal Disease (ADD)**
  4. **Viral Hepatitis (Hepatitis A and Hepatitis E)**
- **Surveillance Categorization:**
  - `suspected_cases`: Clinical syndromic reporting (IDSP Form S).
  - `confirmed_cases`: Laboratory microbiologically verified culture/serology (IDSP Form L).
  - Missing reporting periods are marked as `UNAVAILABLE` or `PROVISIONAL`. Missing values are **never** replaced with zero unless an official zero-case bulletin was formally filed by the District Health Officer.

---

## 3. Dataset Source Provenance Registry Table

Below is the verified registry stored in `dataset_sources.csv` and loaded into the database table `dataset_sources`:

| Source ID | Source Name | Category | Provider URL | Observation Period | Districts | Records | Classification |
|---|---|---|---|---|---|---|---|
| `1` | Central Ground Water Board (CGWB) | `WATER_QUALITY` | `https://cgwb.gov.in/` | 2021–2026 | All 38 Districts | 2,698 | `ACTUAL_HISTORICAL` |
| `2` | India Meteorological Department (IMD) | `WEATHER` | `https://mausam.imd.gov.in/` | 2021–2026 | All 38 Districts | 2,698 | `ACTUAL_HISTORICAL` |
| `3` | Open-Meteo Historical API | `WEATHER` | `https://open-meteo.com/` | 2021–2026 | All 38 Districts | 2,698 | `ACTUAL_HISTORICAL` |
| `4` | Census of India & CAGR Projections | `DEMOGRAPHICS` | `https://censusindia.gov.in/` | 2021–2026 | All 38 Districts | 38 | `ESTIMATED` |
| `5` | IDSP & IHIP Tamil Nadu Surveillance | `DISEASE` | `https://idsp.nic.in/` | 2021–2026 | All 38 Districts | 2,698 | `ACTUAL_HISTORICAL` |

---

## 4. Multi-Layer Storage Architecture

To preserve raw data integrity, AquaSafe enforces 5 segregated data layers:

```
[ LAYER 1: Raw Data ]            --> data/raw/ (Permanent, immutable source CSVs)
        |
        v
[ LAYER 2: Cleaned Data ]        --> data/cleaned/ (Deduplicated, standardized strings)
        |
        v
[ LAYER 3: Big Data Engine ]     --> data/processed/parquet/ (Snappy-compressed Spark Parquet)
        |
        v
[ LAYER 4: ML Training Layer ]   --> ml/training/ (ARFF, feature vectors, train/test split)
        |
        v
[ LAYER 5: Prediction & Serving] --> MySQL 8.x / H2 fallback & REST API cache
```

> [!CAUTION]
> The application is architected so that raw files in `data/raw/` are **read-only**; no ingestion or data processing pipeline is permitted to overwrite raw historical observations.

