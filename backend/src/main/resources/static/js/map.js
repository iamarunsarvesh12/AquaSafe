/**
 * AquaSafe Interactive Risk Map Controller
 * Powered by Leaflet.js covering all 38 Tamil Nadu districts with disease filters
 */

let mapInstance = null;
let markersLayer = null;
let allDistrictsData = [];
let allPredictionsMap = {};

document.addEventListener('DOMContentLoaded', async () => {
    initRiskMap();
    setupDiseaseFilter();
    await loadMapData();
});

function initRiskMap() {
    const mapContainer = document.getElementById('riskMap');
    if (!mapContainer) return;

    // Centered on Tamil Nadu, India
    mapInstance = L.map('riskMap', {
        center: [11.1271, 78.6569],
        zoom: 7,
        minZoom: 6,
        maxZoom: 12
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors | AquaSafe Surveillance'
    }).addTo(mapInstance);

    markersLayer = L.layerGroup().addTo(mapInstance);

    // Map Legend Control
    const legend = L.control({ position: 'bottomright' });
    legend.onAdd = function () {
        const div = L.DomUtil.create('div', 'map-legend p-2 bg-white rounded shadow-sm');
        div.innerHTML = `
            <div class="fw-bold mb-1 small text-dark">Disease Risk Level</div>
            <div><span class="legend-dot" style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#ef4444;margin-right:5px;"></span> High Risk Condition</div>
            <div><span class="legend-dot" style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#f59e0b;margin-right:5px;"></span> Moderate Risk</div>
            <div><span class="legend-dot" style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#10b981;margin-right:5px;"></span> Low Risk Nominal</div>
        `;
        return div;
    };
    legend.addTo(mapInstance);
}

function setupDiseaseFilter() {
    const filterSelect = document.getElementById('diseaseMapFilter');
    if (filterSelect) {
        filterSelect.addEventListener('change', () => {
            renderDistrictMarkers(allDistrictsData, allPredictionsMap, filterSelect.value);
        });
    }
}

async function loadMapData() {
    try {
        let districts = await api.getDistricts();
        if (!districts || districts.length === 0) {
            districts = await api.getPopulation();
        }
        allDistrictsData = districts || [];

        const predictions = await api.getPredictionHistory();
        allPredictionsMap = {};
        if (predictions && predictions.length > 0) {
            predictions.forEach(p => {
                const key = p.district.toLowerCase();
                if (!allPredictionsMap[key]) {
                    allPredictionsMap[key] = p;
                }
            });
        }

        renderDistrictMarkers(allDistrictsData, allPredictionsMap, 'ALL');
        populateDistrictCards(allDistrictsData, allPredictionsMap);
    } catch (err) {
        console.error('Failed to load map data:', err);
    }
}

function renderDistrictMarkers(districts, predMap, diseaseFilter = 'ALL') {
    if (!markersLayer) return;
    markersLayer.clearLayers();

    districts.forEach(d => {
        const distName = d.districtName || d.district;
        const key = distName.toLowerCase();
        const pred = predMap[key] || { riskLevel: 'LOW', probability: 0.75 };

        let risk = pred.riskLevel || 'LOW';
        let prob = ((pred.probability || 0.75) * 100).toFixed(1);

        // Adjust risk if filtered by disease
        if (diseaseFilter === 'CHOLERA' && pred.predictedCholeraRisk) {
            risk = pred.predictedCholeraRisk;
        } else if (diseaseFilter === 'TYPHOID' && pred.predictedTyphoidRisk) {
            risk = pred.predictedTyphoidRisk;
        } else if (diseaseFilter === 'DIARRHEA' && pred.predictedDiarrheaRisk) {
            risk = pred.predictedDiarrheaRisk;
        } else if (diseaseFilter === 'HEPATITIS' && pred.predictedHepatitisRisk) {
            risk = pred.predictedHepatitisRisk;
        }

        let color = '#10b981';
        let badgeClass = 'bg-success';

        if (risk === 'HIGH') {
            color = '#ef4444';
            badgeClass = 'bg-danger';
        } else if (risk === 'MEDIUM') {
            color = '#f59e0b';
            badgeClass = 'bg-warning text-dark';
        }

        const lat = d.latitude;
        const lon = d.longitude;

        if (!lat || !lon) return;

        // Draw interactive circle marker
        const circle = L.circleMarker([lat, lon], {
            radius: risk === 'HIGH' ? 13 : 9,
            fillColor: color,
            color: '#ffffff',
            weight: 2,
            opacity: 1,
            fillOpacity: 0.85
        });

        if (risk === 'HIGH') {
            L.circleMarker([lat, lon], {
                radius: 20,
                fillColor: '#ef4444',
                color: '#ef4444',
                weight: 1,
                opacity: 0.4,
                fillOpacity: 0.2
            }).addTo(markersLayer);
        }

        const popCount = d.population2025 || d.population || 0;
        const sanitation = d.sanitationCoverage || 80.0;

        const popupContent = `
            <div style="min-width: 220px; font-family: 'Inter', sans-serif;">
                <div class="d-flex justify-content-between align-items-center mb-2 border-bottom pb-1">
                    <strong style="font-size: 1.05rem; color: #0f172a;">${escapeHtml(distName)}</strong>
                    <span class="badge ${badgeClass}">${risk} RISK</span>
                </div>
                <div style="font-size: 0.82rem; color: #475569; line-height: 1.6;">
                    <div><strong>Estimated Probability:</strong> ${prob}%</div>
                    <div><strong>Population (2025):</strong> ${popCount.toLocaleString()}</div>
                    <div><strong>Sanitation Coverage:</strong> ${sanitation}%</div>
                    <div><strong>Coordinates:</strong> ${lat.toFixed(4)}, ${lon.toFixed(4)}</div>
                    <div><strong>Disease Focus:</strong> ${diseaseFilter}</div>
                </div>
                <div class="mt-2 pt-1 border-top" style="font-size: 0.72rem; color: #94a3b8;">
                    Source: CGWB / IMD / IDSP Surveillance Feed
                </div>
            </div>
        `;

        circle.bindPopup(popupContent);
        circle.addTo(markersLayer);
    });
}

function populateDistrictCards(districts, predMap) {
    const listEl = document.getElementById('mapDistrictList');
    if (!listEl) return;

    listEl.innerHTML = '';
    districts.forEach(d => {
        const distName = d.districtName || d.district;
        const key = distName.toLowerCase();
        const pred = predMap[key] || { riskLevel: 'LOW', probability: 0.75 };
        const risk = pred.riskLevel || 'LOW';

        let badgeClass = risk === 'HIGH' ? 'bg-danger' : (risk === 'MEDIUM' ? 'bg-warning text-dark' : 'bg-success');

        const card = document.createElement('div');
        card.className = 'p-2 mb-1 border rounded bg-white d-flex justify-content-between align-items-center cursor-pointer';
        card.style.cursor = 'pointer';
        card.innerHTML = `
            <div>
                <strong>${escapeHtml(distName)}</strong>
                <div class="text-muted small">${(d.population2025 || d.population || 0).toLocaleString()} hab</div>
            </div>
            <span class="badge ${badgeClass}">${risk}</span>
        `;
        card.addEventListener('click', () => {
            if (mapInstance && d.latitude && d.longitude) {
                mapInstance.setView([d.latitude, d.longitude], 10);
            }
        });
        listEl.appendChild(card);
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
