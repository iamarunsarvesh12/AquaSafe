# AquaSafe: Data Dictionary & Schema Specification

This document provides the definitive data dictionary for all raw, cleaned, and processed datasets utilized in the **AquaSafe** platform across all 38 districts of Tamil Nadu (2021–2026).

---

## 1. District Master Dataset (`districts_master.csv`)

| Column Name | Data Type | Nullable | Unit / Format | Description | Source / Provenance |
|---|---|---|---|---|---|
| `district_id` | Integer | No | PK (1–38) | Unique numeric identifier for the district | System Generated |
| `district_name` | String (VARCHAR 100) | No | Official Name | Standardized district name normalized across all joins | Survey of India / TN Govt |
| `district_code` | String (VARCHAR 10) | No | Code (e.g. `TN-01`) | Official government ISO/State district code | Revenue Dept Tamil Nadu |
| `latitude` | Float (DOUBLE) | No | Decimal Degrees ($^\circ\text{N}$) | Geographic latitude of district administrative center | Survey of India / GPS |
| `longitude` | Float (DOUBLE) | No | Decimal Degrees ($^\circ\text{E}$) | Geographic longitude of district administrative center | Survey of India / GPS |
| `area_sq_km` | Float (DOUBLE) | No | $\text{km}^2$ | Total geographical area of district | Department of Economics & Stat |
| `population_2011` | Long (BIGINT) | No | Count | Official baseline Census of India 2011 population | Census of India 2011 |
| `growth_rate_pct` | Float (DOUBLE) | No | Annual % (CAGR) | Compound Annual Growth Rate for population estimation | National Commission on Population |
| `population_2021` | Long (BIGINT) | No | Count | Projected district population for year 2021 | CAGR Projection Model |
| `population_2025` | Long (BIGINT) | No | Count | Projected district population for year 2025 | CAGR Projection Model |
| `population_2026` | Long (BIGINT) | No | Count | Projected district population for current year 2026 | CAGR Projection Model |
| `urban_population_pct`| Float (DOUBLE) | No | Percentage (%) | Proportion of district population in urban wards | Census of India / TNSDMA |
| `rural_population_pct`| Float (DOUBLE) | No | Percentage (%) | Proportion of district population in rural panchayats | Census of India / TNSDMA |
| `sanitation_coverage` | Float (DOUBLE) | No | Percentage (%) | Households with functional sanitary latrine coverage | Swachh Bharat Mission (Grameen) |

---

## 2. Water Quality Dataset (`water_quality_raw.csv` / `water_quality_cleaned.csv`)

| Column Name | Data Type | Nullable | Permissible Standard | Unit | Description | Missingness Handling |
|---|---|---|---|---|---|---|
| `record_id` | Long | No | - | PK | Unique record identifier | Not Null |
| `district` | String | No | - | Name | Standardized district name | Required |
| `latitude` | Float | No | - | Degrees | Monitoring sampling location | Required |
| `longitude` | Float | No | - | Degrees | Monitoring sampling location | Required |
| `observation_date` | Date | No | - | `YYYY-MM-DD` | Date of lab analysis or sensor reading | Continuous (Monthly) |
| `ph` | Float | No | 6.5 – 8.5 | pH Scale | Potential of Hydrogen; acid/base balance | Imputed by district baseline median |
| `turbidity` | Float | No | $\le 5.0$ | NTU | Nephelometric Turbidity Units; water clarity | Imputed with seasonal regional mean |
| `tds` | Float | No | $\le 500.0$ | mg/L | Total Dissolved Solids; dissolved minerals | Imputed by district aquifer average |
| `dissolved_oxygen` | Float | No | $\ge 5.0$ | mg/L | Concentration of oxygen dissolved in water | Imputed with water temp equilibrium |
| `electrical_conductivity`| Float | Yes | $\le 1000.0$ | $\mu$S/cm | Ability of water to conduct electrical current | Measured or Estimated from TDS |
| `chloride` | Float | Yes | $\le 250.0$ | mg/L | Chloride anion concentration | Explicit missingness recorded if absent |
| `nitrate` | Float | Yes | $\le 45.0$ | mg/L | Nitrate ($NO_3^-$) from agricultural/sewage runoff | Explicit missingness recorded if absent |
| `fluoride` | Float | Yes | $\le 1.0$ | mg/L | Fluoride concentration (endemic in Dharmapuri) | Explicit missingness recorded if absent |
| `water_temperature`| Float | Yes | - | $^\circ\text{C}$ | Temperature of water at sample collection | Ambient temperature correlation |
| `station_id` | String | No | - | Code | Monitoring station / laboratory tag | CGWB station naming |
| `station_type` | String | No | - | Enum | `GROUNDWATER_LAB` vs `DRINKING_WATER_SENSOR` | Distinguishes lab vs sensor |
| `data_source` | String | No | - | String | Source agency (e.g. `CGWB_TAMILNADU`) | Attributed |
| `missing_parameters`| String | Yes | - | List | Explicit flags of unobserved chemical ions | Audit traceability |

---

## 3. Weather Dataset (`weather_raw.csv` / `weather_cleaned.csv`)

| Column Name | Data Type | Nullable | Range / Unit | Description | Source / Provenance |
|---|---|---|---|---|---|
| `record_id` | Long | No | PK | Unique weather observation identifier | System Generated |
| `district` | String | No | Name | Standardized district name | IMD Station Index |
| `observation_date` | Date | No | `YYYY-MM-DD` | Daily observation timestamp | IMD / Open-Meteo |
| `temp_min` | Float | No | $15.0 - 32.0$ $^\circ\text{C}$ | Minimum daily ambient temperature | IMD Gridded Dataset |
| `temp_max` | Float | No | $24.0 - 44.0$ $^\circ\text{C}$ | Maximum daily ambient temperature | IMD Gridded Dataset |
| `temp_avg` | Float | No | $20.0 - 38.0$ $^\circ\text{C}$ | Average daily ambient temperature | Computed: $(T_{min} + T_{max})/2$ |
| `rainfall` | Float | No | $0.0 - 450.0$ mm | 24-hour accumulated precipitation | IMD Rain Gauge Network |
| `relative_humidity`| Float | No | $20.0 - 100.0$ % | Average daily relative humidity | IMD / Open-Meteo |
| `wind_speed` | Float | Yes | $0.0 - 85.0$ km/h | Mean daily surface wind velocity | Open-Meteo Weather API |
| `latitude` | Float | No | Degrees | IMD weather observatory latitude | Official Station Coordinates |
| `longitude` | Float | No | Degrees | IMD weather observatory longitude | Official Station Coordinates |
| `data_source` | String | No | String | Attribution (`IMD_GRIDDED` or `OPEN_METEO`) | Provenance Registry |

---

## 4. Disease Surveillance Dataset (`disease_surveillance_raw.csv` / `disease_cleaned.csv`)

| Column Name | Data Type | Nullable | Values / Units | Description | Provenance & Rules |
|---|---|---|---|---|---|
| `record_id` | Long | No | PK | Unique epidemiological case record ID | System Generated |
| `district` | String | No | Name | Standardized district surveillance reporting unit | IDSP Reporting District |
| `reporting_date` | Date | No | `YYYY-MM-DD` | Date of surveillance report submission | Periodic Surveillance |
| `disease_name` | String | No | Enum | `Cholera`, `Typhoid`, `Acute Diarrhoeal Disease`, `Hepatitis` | Tracked Pathogen Categories |
| `suspected_cases` | Integer | Yes | Count $\ge 0$ | Clinically suspected symptomatic presentations | IDSP Form S |
| `confirmed_cases` | Integer | Yes | Count $\ge 0$ | Microbiologically or serologically confirmed cases | IDSP Form L (Lab confirmed) |
| `reported_cases` | Integer | No | Count $\ge 0$ | Total official cases reported for the period | Never assumed zero without official report |
| `incidence_rate` | Float | No | Rate per 100,000 | Standardized incidence: $(\text{cases}/\text{pop}) \times 100,000$ | Calculated dynamically |
| `population` | Long | No | Count | Denominator population for the district year | CAGR Projected Census |
| `reporting_period`| String | No | Enum | `DAILY`, `WEEKLY`, `MONTHLY` | Surveillance aggregation window |
| `reporting_status`| String | No | Enum | `PROVISIONAL`, `VERIFIED`, `AUDITED` | Official data verification status |
| `data_source` | String | No | String | Official registry (`IDSP_TAMILNADU`, `IHIP_PORTAL`) | Government Provenance |

---

## 5. Dataset Source Provenance Registry (`dataset_sources.csv`)

| Column Name | Data Type | Description | Example Value |
|---|---|---|---|
| `source_id` | Long | Unique source registry identifier | `1` |
| `source_name` | String | Official name of government organization or provider | `Central Ground Water Board (CGWB)` |
| `source_url` | String | Direct official public access URL or portal reference | `https://cgwb.gov.in/` |
| `category` | String | Domain category (`WATER_QUALITY`, `WEATHER`, `DEMOGRAPHICS`, `DISEASE`) | `WATER_QUALITY` |
| `observation_period`| String | Historical & current temporal coverage | `2021-2026 (5 Complete Years + Current)` |
| `district_coverage` | String | Geographical span across Tamil Nadu | `All 38 Districts` |
| `record_count` | Long | Total number of ingested observations | `2698` |
| `data_format` | String | Original ingested format | `CSV / REST API / Official Bulletin` |
| `validation_status` | String | Data quality validation state | `VALIDATED` |
| `classification` | String | Audit classification: `ACTUAL_HISTORICAL`, `ESTIMATED`, `SYNTHETIC` | `ACTUAL_HISTORICAL` |

---

## 6. Processed Parquet Column Schema (`tamilnadu_epidemiological_analytics.parquet`)

| Parquet Column | Physical Type | Logical Type | Description |
|---|---|---|---|
| `district_name` | BYTE_ARRAY | UTF8 (Partition Key) | Normalized Tamil Nadu district identifier |
| `year` | INT32 | (Partition Key) | Observation year (2021–2026) |
| `month` | INT32 | - | Observation month (1–12) |
| `observation_date` | BYTE_ARRAY | UTF8 | Observation date timestamp |
| `avg_ph` | DOUBLE | - | Monthly mean water pH |
| `avg_turbidity` | DOUBLE | - | Monthly mean water turbidity (NTU) |
| `avg_tds` | DOUBLE | - | Monthly mean Total Dissolved Solids (mg/L) |
| `avg_do` | DOUBLE | - | Monthly mean Dissolved Oxygen (mg/L) |
| `wqi` | DOUBLE | - | Computed Weighted Arithmetic Water Quality Index |
| `wqi_status` | BYTE_ARRAY | UTF8 | Quality label: EXCELLENT, GOOD, POOR, VERY_POOR |
| `monthly_rainfall` | DOUBLE | - | Cumulative monthly precipitation (mm) |
| `avg_temperature` | DOUBLE | - | Mean ambient temperature ($^\circ\text{C}$) |
| `avg_humidity` | DOUBLE | - | Mean ambient relative humidity (%) |
| `total_disease_cases`| INT64 | - | Sum of all water-borne disease cases |
| `incidence_rate_per_100k`| DOUBLE | - | Standardized incidence per 100,000 population |
| `cholera_cases` | INT64 | - | Cases of *Vibrio cholerae* |
| `typhoid_cases` | INT64 | - | Cases of *Salmonella enterica* |
| `diarrhea_cases` | INT64 | - | Cases of Acute Diarrhoeal Disease |
| `hepatitis_cases` | INT64 | - | Cases of Hepatitis A/E |
| `risk_level` | BYTE_ARRAY | UTF8 | Epidemiological risk label: LOW, MEDIUM, HIGH |

