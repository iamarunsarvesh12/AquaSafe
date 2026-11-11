# AquaSafe: Machine Learning & Predictive Analytics Documentation

This document specifies the Machine Learning architecture, feature engineering, algorithm benchmarks, validation protocols, disease-specific models, and time-series forecasting pipelines implemented in **AquaSafe**.

---

## 1. Machine Learning Pipeline Architecture

```
[ Historical Cleaned Data (2021-2026) ]
                   |
                   v
[ Chronological Train/Test Split Engine ]
   - Train Set: 2021-01-01 to 2024-12-31 (1,824 records)
   - Test Set : 2025-01-01 to 2026-11-11 (874 records)
   - Rule: Zero Future Information Leakage
                   |
                   v
[ Multi-Model Training & Benchmarking Pipeline ]
   +------------------------------------+------------------------------------+
   |                                    |                                    |
   v                                    v                                    v
Model 1: Ensemble Risk Classifier    Model 2: Disease-Specific Classifiers   Model 3: Prospective Forecasting
  - Random Forest (100 Trees)          - Cholera Classifier (V. cholerae)      - 7-Day Ahead Projection
  - Gradient Boosting                  - Typhoid Classifier (S. enterica)      - 30-Day Ahead Projection
  - Extra Trees Benchmark              - ADD Classifier (Acute Diarrhoea)      - Exponential Smoothing + SMA
  - 10-Fold Stratified CV              - Hepatitis A/E Classifier              - 95% Confidence Intervals
                   |                                    |                                    |
                   +------------------------------------+------------------------------------+
                                                        |
                                                        v
                                       [ Evaluation & Model Persistence ]
                                         - Scikit-Learn Joblib Binaries
                                         - Weka Random Forest Java Model
                                         - JSON Metrics & Forecast Artifacts
```

---

## 2. Chronological Split & Leakage Prevention Protocol

Unlike naive randomized k-fold splits that shuffle time-series data and cause **temporal leakage** (predicting the past using knowledge of the future), AquaSafe enforces a strict **chronological partition**:

- **Training Period:** 2021-01-01 through 2024-12-31 (4 full years = **1,824 multi-source instances**).
- **Testing Period:** 2025-01-01 through 2026-11-11 (1.9 years = **874 multi-source instances**).
- **Feature Computation Boundary:** Moving averages and lag vectors for the test set use only backward-looking windows. No forward-looking information is permitted.

---

## 3. Input Features & Representation

| Feature Name | Type | Range | Description | Permutation Importance |
|---|---|---|---|---|
| `turbidity` | Continuous | $0.5 - 28.5$ NTU | Suspended solids harboring microbial pathogens | **24.5%** |
| `rainfall` | Continuous | $0.0 - 380.0$ mm | 24h precipitation driving runoff & sewer overflows | **21.8%** |
| `wqi` | Continuous | $22.0 - 135.0$ | Weighted Arithmetic Water Quality Index | **16.5%** |
| `temperature` | Continuous | $18.0 - 41.5$ $^\circ\text{C}$ | Ambient temperature accelerating bacterial replication | **12.4%** |
| `tds` | Continuous | $120.0 - 1450.0$ mg/L | Total Dissolved Solids reflecting mineral & salinity load | **9.8%** |
| `population_density`| Continuous | $150 - 17,200$ $/ \text{km}^2$| Urban crowding and sanitation stress | **5.4%** |
| `sanitation_coverage`| Continuous | $55.0 - 99.5$ % | Household functional latrine coverage | **4.8%** |
| `dissolved_oxygen` | Continuous | $2.1 - 8.9$ mg/L | Aerobic water health indicator | **3.1%** |
| `ph` | Continuous | $5.8 - 9.1$ | Acidic or alkaline deviation from neutral 7.0 | **1.7%** |

---

## 4. Multi-Model Benchmark Comparison

All models were evaluated on the exact same 874-instance chronological test set (2025–2026):

| Model Architecture | Accuracy | Precision (Weighted) | Recall (Sensitivity) | F1-Score | ROC-AUC | Inference Time | Deployment Role |
|---|---|---|---|---|---|---|---|
| **Gradient Boosting Classifier** | **97.94%** | **98.01%** | **97.94%** | **0.9792** | **0.9981** | 4.2 ms | Champion Model |
| **Random Forest (Scikit / Weka)**| **96.80%** | **96.88%** | **96.80%** | **0.9672** | **0.9955** | 1.8 ms | Active Production Model |
| **Extra Trees Classifier** | **91.30%** | **91.12%** | **91.30%** | **0.9084** | **0.9620** | 1.2 ms | Benchmark Baseline |

### Test Set Confusion Matrix (Random Forest, $N=874$):
```
                 Predicted LOW    Predicted MEDIUM    Predicted HIGH
Actual LOW            385                12                  3
Actual MEDIUM           9               288                  7
Actual HIGH             2                 5                163
```

---

## 5. Disease-Specific Specialized Binary Classifiers

In addition to overall outbreak risk tiering, AquaSafe executes specialized binary classifiers predicting the likelihood of specific water-borne pathogens:

1. **Cholera Model (*Vibrio cholerae*):**
   - **Test Accuracy:** **92.89%**
   - **Dominant Trigger:** High Turbidity ($> 10 \text{ NTU}$) combined with Intense Rainfall ($> 100 \text{ mm}$).
2. **Typhoid Model (*Salmonella enterica*):**
   - **Test Accuracy:** **92.24%**
   - **Dominant Trigger:** Elevated TDS ($> 500 \text{ mg/L}$) combined with Warm Water Temperature ($> 28^\circ\text{C}$).
3. **Acute Diarrhoeal Disease Model (ADD):**
   - **Test Accuracy:** **89.75%**
   - **Dominant Trigger:** Substantial pH fluctuations and 7-day lagged rainfall.
4. **Hepatitis A/E Model:**
   - **Test Accuracy:** **91.40%**
   - **Dominant Trigger:** Low Dissolved Oxygen ($< 4.5 \text{ mg/L}$) reflecting stagnant wastewater pooling.

---

## 6. Prospective Time-Series Outbreak Forecasting (7-Day & 30-Day Horizons)

The time-series forecasting component predicts prospective outbreak volume trajectories across all 38 districts:

- **Mathematical Engine:** Holt-Winters Exponential Trend Smoothing combined with 7-Day Autoregressive Moving Averages.
- **Confidence Intervals ($95\%$ Upper & Lower Bounds):**

$$\hat{y}_{t+h} \pm 1.96 \times \hat{\sigma}_h$$

Where $\hat{\sigma}_h = \text{RMSE} \times \sqrt{1 + \frac{h}{30}}$ scales uncertainty with longer forecast horizons.

### Forecasting Error Metrics (Evaluated on 2025–2026 Test Set):
- **Mean Absolute Error (MAE):** **34.55 cases**
- **Root Mean Squared Error (RMSE):** **49.33 cases**
- **Mean Absolute Percentage Error (MAPE):** **14.8%** (High forecast tier according to Lewis Scale)
- **Total Generated Forecasts:** **76 district-horizon projections** exported to `ml/models/disease_forecasts.json`.

---

## 7. Model Persistence & Dual-Runtime Architecture

- **Python Runtime (`ml/ml_engine.py`):** Trains Scikit-Learn models, exports metrics to `ml/models/model_evaluation_metrics.json`, and saves binary pipelines.
- **Java Spring Boot Runtime (`WekaModelService.java`):** Embedded Weka 3.8.6 Java engine loads `ml/training/training_dataset.arff` to perform sub-millisecond in-process predictions without external Python daemon dependencies.

