/**
 * AquaSafe Dashboard Controller
 * Real-time KPI cards, charts and SSE live stream handler
 */

document.addEventListener('DOMContentLoaded', async () => {
    initDashboard();
    setupSseStream();
});

async function initDashboard() {
    try {
        await loadKpiSummary();
        await loadChartsData();
        await loadRecentAlerts();
    } catch (err) {
        console.error('Failed to initialize dashboard:', err);
        showToast('Unable to load dashboard data. Using local cache/stored baseline.', 'warning');
    }
}

async function loadKpiSummary() {
    try {
        const summary = await api.getDashboardSummary();
        updateKpiValues(summary);
    } catch (err) {
        console.warn('Could not fetch KPI summary from backend:', err);
    }
}

function updateKpiValues(summary) {
    if (!summary) return;

    setText('kpiTotalRecords', (summary.totalRecords || 0).toLocaleString());
    setText('kpiTotalDistricts', summary.totalDistricts || 10);
    setText('kpiHighRisk', summary.highRiskDistricts || 0);
    setText('kpiMediumRisk', summary.mediumRiskDistricts || 0);
    setText('kpiLowRisk', summary.lowRiskDistricts || 0);
    setText('kpiAlerts', summary.activeAlertsCount || 0);

    // Update Data Freshness Badge
    const badge = document.getElementById('dataFreshnessBadge');
    const badgeText = document.getElementById('dataFreshnessText');
    if (badge && badgeText) {
        const status = summary.dataFreshness || 'STORED DATA';
        badgeText.textContent = status;
        badge.className = 'freshness-badge ' + (
            status.includes('LIVE') ? 'live' : (status.includes('STORED') ? 'stored' : 'demo')
        );
    }

    // Update Last Updated Timestamp
    const updatedEl = document.getElementById('lastUpdatedTimestamp');
    if (updatedEl && summary.lastUpdated) {
        const d = new Date(summary.lastUpdated);
        updatedEl.textContent = d.toLocaleDateString() + ' ' + d.toLocaleTimeString();
    }
}

async function loadChartsData() {
    try {
        // 1. Disease Trends Chart
        const trends = await api.getDiseaseTrends();
        AquaCharts.initDiseaseTrend('diseaseTrendChart', trends);

        // 2. Risk Distribution Chart
        const riskDist = await api.getRiskDistribution();
        AquaCharts.initRiskDistribution('riskDistChart', riskDist);

        // 3. District Risk Bar Chart
        const districtRisk = await api.getDistrictRisk();
        AquaCharts.initDistrictRiskBar('districtRiskChart', districtRisk);

        // 4. Rainfall vs Disease Correlation
        AquaCharts.initScatterCorrelation('scatterCorrChart');
    } catch (err) {
        console.error('Chart loading error:', err);
    }
}

async function loadRecentAlerts() {
    const list = document.getElementById('recentAlertsList');
    if (!list) return;

    try {
        const alerts = await api.getAlerts();
        if (!alerts || alerts.length === 0) {
            list.innerHTML = `
                <div class="p-3 text-center text-muted">
                    <i class="fa-solid fa-circle-check text-success fa-2x mb-2"></i>
                    <p class="mb-0 small">No active critical alerts. All monitored parameters are within nominal thresholds.</p>
                </div>`;
            return;
        }

        list.innerHTML = alerts.slice(0, 4).map(a => `
            <div class="list-group-item d-flex align-items-start gap-3 py-3 border-0 border-bottom">
                <div class="kpi-icon ${a.riskLevel === 'HIGH' ? 'bg-danger-subtle text-danger' : 'bg-warning-subtle text-warning'}">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                </div>
                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <strong class="text-dark small">${escapeHtml(a.district)}</strong>
                        <span class="badge ${a.riskLevel === 'HIGH' ? 'bg-danger' : 'bg-warning text-dark'}">${a.riskLevel} RISK</span>
                    </div>
                    <p class="mb-1 text-muted small">${escapeHtml(a.message)}</p>
                    <small class="text-secondary">${new Date(a.createdAt).toLocaleString()}</small>
                </div>
            </div>
        `).join('');
    } catch (err) {
        console.warn('Could not load alerts list:', err);
    }
}

function setupSseStream() {
    const sseUrl = `${API_BASE}/dashboard/live-stream`;
    try {
        const eventSource = new EventSource(sseUrl);

        eventSource.addEventListener('INIT', (event) => {
            const data = JSON.parse(event.data);
            updateKpiValues(data);
        });

        eventSource.addEventListener('DASHBOARD_UPDATE', (event) => {
            const data = JSON.parse(event.data);
            updateKpiValues(data);
            const liveDot = document.getElementById('liveIndicatorDot');
            if (liveDot) {
                liveDot.style.display = 'inline-block';
            }
        });

        eventSource.onerror = () => {
            console.debug('SSE disconnected or not available, using periodic REST updates.');
            eventSource.close();
        };
    } catch (e) {
        console.debug('SSE initialization caught exception:', e);
    }
}

function setText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = text;
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function showToast(message, type = 'info') {
    console.log(`[Toast ${type}]: ${message}`);
}
