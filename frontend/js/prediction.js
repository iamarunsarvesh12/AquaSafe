/**
 * AquaSafe Machine Learning Prediction Controller
 * Submits environmental/sanitary features to Weka Random Forest model
 */

const DISTRICT_PRESETS = {
    'Chennai': {
        ph: 6.8, turbidity: 7.2, tds: 380, dissolvedOxygen: 5.2,
        temperature: 29.5, rainfall: 85.0, humidity: 82.0,
        previousDiseaseCases: 28, populationDensity: 26900, sanitationCoverage: 92.5
    },
    'Salem': {
        ph: 6.2, turbidity: 9.4, tds: 440, dissolvedOxygen: 4.3,
        temperature: 30.1, rainfall: 72.0, humidity: 76.0,
        previousDiseaseCases: 34, populationDensity: 665, sanitationCoverage: 75.5
    },
    'Coimbatore': {
        ph: 7.3, turbidity: 2.1, tds: 220, dissolvedOxygen: 7.1,
        temperature: 26.5, rainfall: 12.0, humidity: 62.0,
        previousDiseaseCases: 4, populationDensity: 748, sanitationCoverage: 88.5
    },
    'Kanyakumari': {
        ph: 7.4, turbidity: 1.8, tds: 195, dissolvedOxygen: 7.8,
        temperature: 27.2, rainfall: 8.0, humidity: 65.0,
        previousDiseaseCases: 2, populationDensity: 1119, sanitationCoverage: 91.0
    }
};

document.addEventListener('DOMContentLoaded', () => {
    setupPredictionForm();
    loadPredictionHistory();
});

function setupPredictionForm() {
    const form = document.getElementById('predictionForm');
    const districtSelect = document.getElementById('predDistrict');

    // Quick Preset buttons
    document.querySelectorAll('.preset-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const district = btn.getAttribute('data-district');
            applyPreset(district);
        });
    });

    if (districtSelect) {
        districtSelect.addEventListener('change', (e) => {
            if (DISTRICT_PRESETS[e.target.value]) {
                applyPreset(e.target.value);
            }
        });
    }

    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await executePrediction();
        });
    }
}

function applyPreset(district) {
    const data = DISTRICT_PRESETS[district];
    if (!data) return;

    document.getElementById('predDistrict').value = district;
    document.getElementById('predPh').value = data.ph;
    document.getElementById('predTurbidity').value = data.turbidity;
    document.getElementById('predTds').value = data.tds;
    document.getElementById('predDo').value = data.dissolvedOxygen;
    document.getElementById('predTemp').value = data.temperature;
    document.getElementById('predRainfall').value = data.rainfall;
    document.getElementById('predHumidity').value = data.humidity;
    document.getElementById('predCases').value = data.previousDiseaseCases;
    document.getElementById('predDensity').value = data.populationDensity;
    document.getElementById('predSanitation').value = data.sanitationCoverage;
}

async function executePrediction() {
    const btn = document.getElementById('btnPredictRisk');
    const spinner = document.getElementById('predictSpinner');
    const resultBox = document.getElementById('predictionResultContainer');

    const params = {
        district: document.getElementById('predDistrict').value,
        ph: parseFloat(document.getElementById('predPh').value),
        turbidity: parseFloat(document.getElementById('predTurbidity').value),
        tds: parseFloat(document.getElementById('predTds').value),
        dissolvedOxygen: parseFloat(document.getElementById('predDo').value),
        temperature: parseFloat(document.getElementById('predTemp').value),
        rainfall: parseFloat(document.getElementById('predRainfall').value),
        humidity: parseFloat(document.getElementById('predHumidity').value),
        previousDiseaseCases: parseInt(document.getElementById('predCases').value),
        populationDensity: parseInt(document.getElementById('predDensity').value),
        sanitationCoverage: parseFloat(document.getElementById('predSanitation').value)
    };

    if (btn) btn.disabled = true;
    if (spinner) spinner.classList.remove('d-none');

    try {
        const response = await api.predictRisk(params);
        renderPredictionResult(response, params);
        await loadPredictionHistory();
    } catch (err) {
        alert('Prediction Error: ' + err.message);
    } finally {
        if (btn) btn.disabled = false;
        if (spinner) spinner.classList.add('d-none');
    }
}

function renderPredictionResult(res, params) {
    const container = document.getElementById('predictionResultContainer');
    if (!container) return;

    const risk = (res.riskLevel || 'LOW').toUpperCase();
    const probPercent = (res.probability * 100).toFixed(1);

    let riskClass = 'low';
    let riskBg = 'risk-low';
    let badgeColor = 'bg-success';
    if (risk === 'HIGH') {
        riskClass = 'high';
        riskBg = 'risk-high';
        badgeColor = 'bg-danger';
    } else if (risk === 'MEDIUM') {
        riskClass = 'medium';
        riskBg = 'risk-medium';
        badgeColor = 'bg-warning text-dark';
    }

    // Class probabilities
    const cp = res.classProbabilities || { LOW: 0, MEDIUM: 0, HIGH: 0 };
    const lowP = ((cp.LOW || 0) * 100).toFixed(1);
    const medP = ((cp.MEDIUM || 0) * 100).toFixed(1);
    const highP = ((cp.HIGH || 0) * 100).toFixed(1);

    container.innerHTML = `
        <div class="result-card ${riskBg} shadow-sm animate__animated animate__fadeIn">
            <span class="text-uppercase fw-bold text-muted small letter-spacing-1">Estimated Risk Assessment</span>
            <div class="my-3">
                <span class="risk-badge-large ${riskClass}">${risk} RISK</span>
            </div>
            
            <h3 class="fw-bold mb-2 text-dark">${probPercent}% Estimated Probability</h3>
            <p class="text-secondary mb-4">${escapeHtml(res.message)}</p>

            <!-- Probability breakdown bars -->
            <div class="row g-2 mb-4 text-start">
                <div class="col-4">
                    <div class="small fw-semibold text-muted mb-1">LOW: ${lowP}%</div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-success" style="width: ${lowP}%"></div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="small fw-semibold text-muted mb-1">MEDIUM: ${medP}%</div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-warning" style="width: ${medP}%"></div>
                    </div>
                </div>
                <div class="col-4">
                    <div class="small fw-semibold text-muted mb-1">HIGH: ${highP}%</div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-danger" style="width: ${highP}%"></div>
                    </div>
                </div>
            </div>

            <!-- Model & Input Snapshot Metadata -->
            <div class="bg-white p-3 rounded-3 text-start small border">
                <div class="row g-2">
                    <div class="col-md-6"><strong>District:</strong> ${escapeHtml(res.district)}</div>
                    <div class="col-md-6"><strong>Model Engine:</strong> ${escapeHtml(res.modelVersion)} (Weka Random Forest)</div>
                    <div class="col-md-6"><strong>Timestamp:</strong> ${new Date(res.predictionDate).toLocaleString()}</div>
                    <div class="col-md-6"><strong>Input Turbidity:</strong> ${params.turbidity} NTU | <strong>DO:</strong> ${params.dissolvedOxygen} mg/L</div>
                </div>
            </div>

            <!-- Disclaimer -->
            <div class="disclaimer-box text-start">
                <i class="fa-solid fa-circle-info text-primary mt-1"></i>
                <div>
                    <strong>Public Health Public Notice:</strong> ${escapeHtml(res.disclaimer)}
                </div>
            </div>
        </div>
    `;

    container.scrollIntoView({ behavior: 'smooth' });
}

async function loadPredictionHistory() {
    const tbody = document.getElementById('predictionHistoryBody');
    if (!tbody) return;

    try {
        const history = await api.getPredictionHistory();
        if (!history || history.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-3">No prediction records available.</td></tr>`;
            return;
        }

        tbody.innerHTML = history.slice(0, 8).map(h => {
            let badge = 'bg-success';
            if (h.riskLevel === 'HIGH') badge = 'bg-danger';
            else if (h.riskLevel === 'MEDIUM') badge = 'bg-warning text-dark';

            return `
                <tr>
                    <td><strong>${escapeHtml(h.district)}</strong></td>
                    <td><span class="badge ${badge}">${h.riskLevel}</span></td>
                    <td>${(h.probability * 100).toFixed(1)}%</td>
                    <td><span class="badge bg-light text-dark">${escapeHtml(h.modelVersion)}</span></td>
                    <td>${new Date(h.predictionDate).toLocaleString()}</td>
                    <td>
                        <span class="badge bg-light text-muted">pH: ${h.ph || '-'}, Turb: ${h.turbidity || '-'}</span>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        console.warn('Could not load prediction history:', err);
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
