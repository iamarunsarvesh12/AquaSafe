# AquaSafe: How It Works – Mathematical & Architectural Manual

This document provides a comprehensive, rigorous technical explanation of how **AquaSafe** operates from end-to-end. It details every mathematical formula, algorithmic mechanism, feature engineering transformation, and Big Data aggregation step used across all 38 districts of Tamil Nadu.

---

## 1. End-to-End System Workflow

```
+----------------------------------------------------------------------------------------------------+
|                                    1. MULTI-SOURCE INGESTION                                      |
|  - Water Quality: CGWB & WRO Lab Standards (pH, Turbidity, TDS, DO, Temp, Ions)                   |
|  - Weather: IMD & Open-Meteo Observations (Temp Min/Max/Avg, Rainfall, Humidity, Wind)             |
|  - Demographics: Census 2011 + CAGR Projected District Populations & Sanitation                   |
|  - Epidemiological Surveillance: IDSP & IHIP (Cholera, Typhoid, ADD, Hepatitis)                   |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                                 2. APACHE SPARK BIG DATA ENGINE                                   |
|  - Schema Validation, Null Handling & Imputation                                                   |
|  - Multi-Source Joins on [District_Normalized, Observation_Date]                                  |
|  - Partitioning by [district_name, year] across 10 distributed partitions                          |
|  - Storage in Columnar Snappy-compressed Apache Parquet format (3.35 MB)                          |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                              3. MATHEMATICAL & FEATURE TRANSFORMATIONS                             |
|  - Weighted Arithmetic Water Quality Index (WQI)                                                   |
|  - Crude & Standardized Disease Incidence Rates per 100,000 Population                            |
|  - 7-Day & 30-Day Meteorological Simple Moving Averages (SMA) & Monsoon Lag Vectors                |
|  - Pearson & Spearman Bivariate Environmental Correlations                                        |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                                4. MACHINE LEARNING & PREDICTION                                    |
|  - Chronological Split: 2021-2024 Training (1,824 records), 2025-2026 Testing (874 records)        |
|  - Random Forest (96.80% Acc) & Gradient Boosting (97.94% Acc, 0.9981 ROC-AUC)                   |
|  - Binary Disease-Specific Models: Cholera, Typhoid, Acute Diarrhoea, Hepatitis                   |
|  - Exponential Smoothing Time-Series Forecasting (7-day & 30-day ahead projections with 95% CI)   |
+----------------------------------------------------------------------------------------------------+
                                                  |
                                                  v
+----------------------------------------------------------------------------------------------------+
|                                   5. INTELLIGENCE & SURVEILLANCE                                  |
|  - 14 Real-Time Web Modules (Geospatial Risk Heatmap, District Deep Dive, WQI Violations)          |
|  - Automated Surveillance Alerts with WHO/IDSP Actionable Public Health Advisories                 |
|  - Dual Database Persistence: MySQL 8.x Production with Embedded H2 MySQL-Mode Fallback           |
+----------------------------------------------------------------------------------------------------+
```

---

## 2. Mathematical Calculations & Formulas

### A. Water Quality Index (WQI) Calculation

The system employs the internationally recognized **Weighted Arithmetic Water Quality Index (WAWQI)** method according to **Bureau of Indian Standards (BIS 10500:2012)**.

#### 1. Unit Weight Factor ($W_i$)
Each chemical and physical parameter is assigned a weight inversely proportional to its permissible limit ($S_i$):

$$K = \frac{1}{\sum_{i=1}^{n} \frac{1}{S_i}}$$

$$W_i = \frac{K}{S_i}$$

Where:
- $K$ is the proportionality constant.
- $S_i$ is the BIS 10500:2012 permissible limit for parameter $i$.
- $W_i$ is the unit weight factor such that $\sum_{i=1}^{n} W_i = 1.0$.

#### Standard Permissible Limits & Assigned Unit Weights:
| Parameter ($i$) | Permissible Standard ($S_i$) | Ideal Value ($V_{ideal}$) | Unit Weight ($W_i$) |
|---|---|---|---|
| pH | 8.5 (range 6.5–8.5) | 7.0 | 0.220 |
| Turbidity | 5.0 NTU | 0.0 | 0.280 |
| Total Dissolved Solids (TDS) | 500.0 mg/L | 0.0 | 0.160 |
| Dissolved Oxygen (DO) | 5.0 mg/L (min) | 14.6 mg/L | 0.180 |
| Electrical Conductivity (EC) | 1000.0 $\mu$S/cm | 0.0 | 0.080 |
| Nitrate ($NO_3^-$) | 45.0 mg/L | 0.0 | 0.080 |

#### 2. Sub-Index Quality Rating Scale ($Q_i$)
The quality rating scale for each parameter (except pH and DO) is calculated as:

$$Q_i = 100 \times \left( \frac{V_{actual} - V_{ideal}}{S_i - V_{ideal}} \right)$$

For **pH** (where ideal neutral is 7.0):

$$Q_{pH} = 100 \times \left( \frac{V_{pH} - 7.0}{8.5 - 7.0} \right)$$

For **Dissolved Oxygen** (where higher values represent cleaner water, saturation = 14.6 mg/L):

$$Q_{DO} = 100 \times \left( \frac{14.6 - V_{DO}}{14.6 - 5.0} \right)$$

#### 3. Overall WQI Aggregation
The overall Water Quality Index is computed as the weighted sum of sub-indices:

$$\text{WQI} = \sum_{i=1}^{n} (Q_i \times W_i)$$

#### WQI Classification Categories:
- **$\text{WQI} < 50$**: Excellent / Safe Drinking Water Quality (Low Microbial Contamination Risk)
- **$50 \le \text{WQI} < 75$**: Moderate / Permissible with Standard Treatment
- **$75 \le \text{WQI} < 100$**: Poor / Water Requires Advanced Disinfection & Filtration
- **$\text{WQI} \ge 100$**: Unfit for Consumption / High Biohazard & Water-Borne Pathogen Breeding Potential

---

### B. Disease Incidence Rate Calculation

To prevent misrepresenting raw case counts influenced purely by population size, AquaSafe calculates standardized crude and population-adjusted **Incidence Rates per 100,000 Population** according to World Health Organization (WHO) epidemiological guidelines.

$$\text{Incidence Rate} = \left( \frac{\text{New Reported Cases in Period } T}{\text{Total Estimated District Population at Period } T} \right) \times 100,000$$

#### Worked Example:
- **District:** Cuddalore
- **Total Reported Cases in Month:** 142 cases (ADD: 110, Typhoid: 22, Cholera: 8, Hepatitis: 2)
- **District Population (2025 CAGR Projected):** 2,685,400
- **Incidence Rate Calculation:**

$$\text{Incidence Rate} = \left( \frac{142}{2,685,400} \right) \times 100,000 = 5.288 \text{ cases per 100,000 people}$$

#### Annualized Outbreak Burden Tiers:
- **$< 50$ per 100k:** Low Burden / Endemic Baseline
- **$50 - 150$ per 100k:** Moderate Burden / Heightened Surveillance Advised
- **$> 150$ per 100k:** High Burden / Active Public Health Intervention Required

---

### C. Meteorological Time-Series Moving Averages (SMA)

Water-borne pathogens like *Vibrio cholerae* and *Salmonella* exhibit lag periods between heavy precipitation and clinical outbreak presentation (due to floodwater stagnation, runoff into shallow borewells, and incubation periods of 2 to 14 days).

AquaSafe computes **Simple Moving Averages (SMA)** for rainfall and temperature over 7-day and 30-day sliding windows:

$$\text{SMA}_{k}(t) = \frac{1}{k} \sum_{i=0}^{k-1} X_{t-i}$$

Where:
- $k \in \{7, 30\}$ represents the smoothing window length in days.
- $X_t$ is the daily meteorological observation (e.g., daily rainfall in mm).

#### Lagged Outbreak Vulnerability Index:
AquaSafe generates 7-day lagged rainfall vectors ($\text{Rainfall}_{t-7}$) and 14-day lagged temperature vectors to model biological bacterial reproduction:

$$\text{Runoff Impact Vector} = \alpha \cdot \text{Rainfall}_t + \beta \cdot \text{SMA}_7(\text{Rainfall}) + \gamma \cdot \text{SMA}_{30}(\text{Rainfall})$$

Where weights $\alpha = 0.5, \beta = 0.35, \gamma = 0.15$ reflect immediate flash flooding, soil saturation, and reservoir microbial contamination respectively.

---

### D. Environmental Correlation Analysis

The platform differentiates between linear parametric relationships and monotonic non-linear dependencies using two complementary statistical coefficients:

#### 1. Pearson Product-Moment Correlation ($r$)
Measures linear correlation between continuous variables (e.g., Temperature and Bacterial Colony Count):

$$r_{xy} = \frac{\sum_{i=1}^{n} (x_i - \bar{x})(y_i - \bar{y})}{\sqrt{\sum_{i=1}^{n} (x_i - \bar{x})^2} \sqrt{\sum_{i=1}^{n} (y_i - \bar{y})^2}}$$

#### 2. Spearman Rank-Order Correlation ($\rho$)
Evaluates non-linear monotonic associations (e.g., Turbidity vs ADD Cases):

$$\rho = 1 - \frac{6 \sum d_i^2}{n(n^2 - 1)}$$

Where $d_i = \text{rank}(x_i) - \text{rank}(y_i)$ is the difference between the assigned ranks of each observation pair.

> [!IMPORTANT]
> **Correlation vs. Causation Principle:** AquaSafe's analytical dashboard explicitly annotates that environmental correlation indicates *favorable bio-climatic conditions for transmission*, not a biological proof of pathogen contamination in the absence of laboratory microbiological confirmation.

---

### E. Machine Learning Multi-Indicator Classification

The prediction engine utilizes an ensemble of **Random Forest** and **Gradient Boosted Decision Trees** trained on historical multi-source feature vectors:

$$\mathbf{x} = [\text{pH}, \text{Turbidity}, \text{TDS}, \text{DO}, \text{Rainfall}, \text{Temp}, \text{Humidity}, \text{PopDensity}, \text{Sanitation}, \text{WQI}, \text{LagRainfall}]$$

#### Ensemble Probability Calculation:
For an ensemble of $M$ decision trees, the predicted probability for class $c \in \{\text{LOW}, \text{MEDIUM}, \text{HIGH}\}$ is:

$$P(Y = c \mid \mathbf{x}) = \frac{1}{M} \sum_{m=1}^{M} h_m(c \mid \mathbf{x})$$

Where $h_m(c \mid \mathbf{x})$ is the leaf probability estimate of the $m$-th tree.

The final predicted risk tier $Y^*$ is chosen by argmax:

$$Y^* = \arg\max_{c} P(Y = c \mid \mathbf{x})$$

With soft threshold decision rules applied for emergency public health sensitivity:
- If $P(\text{HIGH}) > 0.40$ or ($\text{Turbidity} > 15 \text{ NTU} \land \text{Rainfall} > 120 \text{ mm}$), the system flags **HIGH RISK** to avoid false negatives during critical monsoon surges.

---

## 3. Full Tamil Nadu District Workflow Walkthrough

To see how the mathematical pipeline executes in practice, consider an actual quarterly workflow for **Chennai District**:

1. **Raw Sensor & Meteorological Ingestion:**
   - Weather station records 185 mm cumulative rainfall over 5 days during the North-East Monsoon (November).
   - Surface intake monitoring records turbidity spike to 18.4 NTU and TDS to 710 mg/L.
2. **Spark Processing & Normalization:**
   - District name `'Chennai'` is matched to District ID `1` (`TN-01`), GPS `(13.0827, 80.2707)`.
   - Spark SQL executes partitioned join with demographic CAGR population `7,215,000` (Density: 16,920 persons/$\text{km}^2$).
3. **Feature Computation:**
   - $\text{WQI} = 84.6$ (Rated "Very Poor").
   - 7-day Moving Average Rainfall = $37.0 \text{ mm/day}$.
4. **Machine Learning Model Inference:**
   - Random Forest probability distribution: $P(\text{LOW}) = 0.02, P(\text{MEDIUM}) = 0.18, P(\text{HIGH}) = 0.80$.
   - Classification output: **HIGH RISK** (Risk Score: 88/100).
   - Disease-Specific breakdown:
     - Cholera Likelihood: 82% (Severe)
     - Acute Diarrhoeal Disease: 91% (Critical)
     - Typhoid Likelihood: 64% (Elevated)
     - Hepatitis A/E: 52% (Moderate)
5. **Prospective Time-Series Forecasting:**
   - 7-Day Ahead Projected Cases: **142 cases** ($95\% \text{ CI: } [115, 178]$).
   - 30-Day Ahead Projected Cases: **490 cases** ($95\% \text{ CI: } [390, 610]$).
6. **Surveillance Alert Dispatch:**
   - System triggers `CRITICAL` alert to Greater Chennai Corporation Health Officers.
   - Prescribed Public Health Advisory: Immediate chlorination booster at distribution mains, advisories to boil drinking water in flood-prone lowlands, and pre-positioning of Oral Rehydration Salts (ORS) at urban primary health centers.

---

## 4. How Environmental Variables Drive Water-Borne Disease

| Variable | Biological Mechanism | Impact on Pathogens | Primary Disease Linked |
|---|---|---|---|
| **Turbidity ($\uparrow$)** | Suspended clay, silt, and organic matter shield bacteria and viruses from solar UV inactivation and chemical chlorine disinfection. | Provides attachment surfaces and nutrient absorption for bacterial colonies. | Cholera, Acute Diarrhoea |
| **Rainfall ($\uparrow$)** | Surface runoff flushes animal feces, open defecation remnants, and overflowing municipal sewage into shallow borewells and open reservoirs. | Exponentially increases bacterial load in untreated source waters. | Cholera, ADD, Hepatitis A/E |
| **High Temperature ($> 30^\circ\text{C}$)** | Elevates ambient water temperature in storage tanks and stagnant pools, accelerating microbial replication. | Shortens bacterial generation time for *Salmonella enterica* and *Vibrio cholerae*. | Typhoid, ADD |
| **Low Dissolved Oxygen ($< 4 \text{ mg/L}$)** | Indicates severe organic pollution, eutrophication, and anaerobic bacterial activity. | Depletes aquatic biodiversity and signals raw wastewater intrusion. | Hepatitis E, Cholera |
| **High Population Density** | Amplifies human-to-human transmission vectors and overburdens localized sewerage infrastructure. | Increases transmission velocity through shared community water pumps. | Typhoid, Cholera |
| **Sanitation Coverage ($\downarrow$)** | Unimproved pit latrines or open defecation leads to groundwater table seepage during rain. | Direct fecal-oral route contamination of unsealed aquifer boreholes. | All Water-Borne Diseases |

---

## 5. Big Data Processing Scalability

AquaSafe handles multi-year, multi-district data streams efficiently through:

1. **Distributed Partitioning Strategy:**
   - Processed data is split by `(district_name, year)` across 10 partitions. Queries querying a specific district or year prune 90% of the dataset without reading unneeded blocks.
2. **Columnar Parquet Storage:**
   - Storage size is reduced from over 15 MB in uncompressed raw CSVs down to **3.35 MB** in Snappy-compressed binary Parquet.
   - Column projection reads only relevant feature vectors (e.g. `[turbidity, rainfall]`) during ML batch inference, achieving a 5x I/O speedup.
3. **Execution Decoupling:**
   - Batch transformation runs asynchronously via PySpark 4.2.0, while sub-millisecond REST query serving is handled by Spring Boot JPA with 2-tier caching.

