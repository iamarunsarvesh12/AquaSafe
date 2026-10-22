/**
 * AquaSafe Interactive Risk Map Controller
 * Powered by Leaflet.js focused on Tamil Nadu districts
 */

let mapInstance = null;
let markersLayer = null;

document.addEventListener('DOMContentLoaded', async () => {
    initRiskMap();
    await loadMapData();
});

function initRiskMap() {
    const mapContainer = document.getElementById('riskMap');
    if (!mapContainer) return;

    // Centered on Tamil Nadu, India (Coordinates: 11.0, 78.5)
    mapInstance = L.map('riskMap', {
        center: [11.1271, 78.6569],
        zoom: 7,
        minZoom: 6,
        maxZoom: 12
    });

    // Clean OpenStreetMap Tile Layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors | AquaSafe Intelligence'
    }).addTo(mapInstance);

    markersLayer = L.layerGroup().addTo(mapInstance);

    // Map Legend Control
    const legend = L.control({ position: 'bottomright' });
    legend.onAdd = function () {
        const div = L.DomUtil.create('div', 'map-legend');
        div.innerHTML = `
            <div class="fw-bold mb-1 small text-dark">Estimated Risk Level</div>
            <div><span class="legend-dot" style="background:#ef4444;"></span> High Risk Condition</div>
            <div><span class="legend-dot" style="background:#f59e0b;"></span> Moderate Risk</div>
            <div><span class="legend-dot" style="background:#10b981;"></span> Low Risk Nominal</div>
        `;
        return div;
    };
    legend.addTo(mapInstance);
}

async function loadMapData() {
    try {
        // Fetch population & coordinates data
        const districts = await api.getPopulation();
        // Fetch latest prediction per district
        const summary = await api.getDashboardSummary();
        const predictions = await api.getPredictionHistory();

        // Build a lookup map of district -> latest prediction
        const predMap = {};
        if (predictions && predictions.length > 0) {
            predictions.forEach(p => {
                if (!predMap[p.district.toLowerCase()]) {
                    predMap[p.district.toLowerCase()] = p;
                }
            });
        }

        renderDistrictMarkers(districts, predMap);
    } catch (err) {
        console.error('Failed to load map data:', err);
    }
}

function renderDistrictMarkers(districts, predMap) {
    if (!markersLayer) return;
    markersLayer.clearLayers();

    // Fallback baseline risk if prediction not yet in history
    const baselineRisks = {
        'salem': { riskLevel: 'HIGH', probability: 0.842 },
        'thanjavur': { riskLevel: 'HIGH', probability: 0.793 },
        'chennai': { riskLevel: 'MEDIUM', probability: 0.684 },
        'tiruchirappalli': { riskLevel: 'MEDIUM', probability: 0.621 },
        'vellore': { riskLevel: 'MEDIUM', probability: 0.651 },
        'madurai': { riskLevel: 'LOW', probability: 0.812 },
        'coimbatore': { riskLevel: 'LOW', probability: 0.895 },
        'tirunelveli': { riskLevel: 'LOW', probability: 0.778 },
        'erode': { riskLevel: 'LOW', probability: 0.835 },
        'kanyakumari': { riskLevel: 'LOW', probability: 0.912 }
    };

    districts.forEach(d => {
        const key = d.district.toLowerCase();
        const pred = predMap[key] || baselineRisks[key] || { riskLevel: 'LOW', probability: 0.75 };

        const risk = pred.riskLevel || 'LOW';
        const prob = ((pred.probability || 0.75) * 100).toFixed(1);

        let color = '#10b981';
        let fillColor = '#ecfdf5';
        let badgeClass = 'bg-success';

        if (risk === 'HIGH') {
            color = '#ef4444';
            fillColor = '#fee2e2';
            badgeClass = 'bg-danger';
        } else if (risk === 'MEDIUM') {
            color = '#f59e0b';
            fillColor = '#fef3c7';
            badgeClass = 'bg-warning text-dark';
        }

        // Draw interactive circle marker
        const circle = L.circleMarker([d.latitude, d.longitude], {
            radius: risk === 'HIGH' ? 14 : 11,
            fillColor: color,
            color: '#ffffff',
            weight: 2,
            opacity: 1,
            fillOpacity: 0.85
        });

        // Pulsing animation effect for HIGH risk districts
        if (risk === 'HIGH') {
            const pulse = L.circleMarker([d.latitude, d.longitude], {
                radius: 22,
                fillColor: '#ef4444',
                color: '#ef4444',
                weight: 1,
                opacity: 0.4,
                fillOpacity: 0.2
            }).addTo(markersLayer);
        }

        const popupContent = `
            <div style="min-width: 220px; font-family: 'Inter', sans-serif;">
                <div class="d-flex justify-content-between align-items-center mb-2 border-bottom pb-1">
                    <strong style="font-size: 1.05rem; color: #0f172a;">${escapeHtml(d.district)}</strong>
                    <span class="badge ${badgeClass}">${risk} RISK</span>
                </div>
                <div style="font-size: 0.82rem; color: #475569; line-height: 1.6;">
                    <div><strong>Estimated Probability:</strong> ${prob}%</div>
                    <div><strong>Population:</strong> ${d.population.toLocaleString()}</div>
                    <div><strong>Sanitation Coverage:</strong> ${d.sanitationCoverage}%</div>
                    <div><strong>Coordinates:</strong> ${d.latitude.toFixed(4)}, ${d.longitude.toFixed(4)}</div>
                    <div><strong>Last Updated:</strong> ${new Date().toLocaleDateString()}</div>
                </div>
                <div class="mt-2 pt-1 border-top" style="font-size: 0.72rem; color: #94a3b8;">
                    Source: Public Health Demo Data
                </div>
            </div>
        `;

        circle.bindPopup(popupContent);
        circle.addTo(markersLayer);
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
