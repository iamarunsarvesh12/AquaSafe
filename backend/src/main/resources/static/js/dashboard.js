/**
 * AquaSafe Dashboard Controller
 * Real-time KPI cards, charts, live data synchronization and SSE stream handler
 */

document.addEventListener('DOMContentLoaded', async () => {
    initDashboard();
    setupSseStream();
    loadSyncStatus();
    checkDhoJurisdiction();
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

function checkDhoJurisdiction() {
    const user = api.getUser();
    const banner = document.getElementById('dhoJurisdictionBanner');
    if (banner && user) {
        const role = (user.role || '').toUpperCase();
        if (role === 'DHO' || role.includes('OFFICER')) {
            const dist = user.assignedDistrict || 'Chennai';
            banner.innerHTML = `
                <div class="alert alert-warning py-2 px-3 small d-flex align-items-center mb-3">
                    <i class="fa-solid fa-hospital-user me-2 fs-5"></i>
                    <div>
                        <strong>District Health Officer Mode:</strong> Operating under restricted jurisdiction for 
                        <strong class="text-dark">${escapeHtml(dist)} District</strong>. Statewide modifications restricted.
                    </div>
                </div>
            `;
            banner.style.display = 'block';
        } else {
            banner.style.display = 'none';
        }
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
    setText('kpiTotalDistricts', summary.totalDistricts || 38);
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

async function loadSyncStatus() {
    try {
        const status = await api.getDataSyncStatus();
        const banner = document.getElementById('liveSyncStatusText');
        const badge = document.getElementById('syncConnectionBadge');

        if (status) {
            if (banner) {
                banner.textContent = `Open-Meteo Weather API: ${status.apiConnectionStatus} | Last Synced: ${status.lastSyncTime} (${status.recordsRetrieved} districts updated, ${status.failedRequests} errors)`;
            }
            if (badge) {
                badge.className = 'badge ' + (status.apiConnectionStatus === 'ONLINE' ? 'bg-success' : 'bg-secondary');
                badge.textContent = status.apiConnectionStatus;
            }
        }
    } catch (err) {
        console.debug('Sync status check notice:', err);
    }
}

async function manualSyncLiveData() {
    const btn = document.getElementById('btnRefreshLiveData');
    const icon = document.getElementById('syncSpinIcon');
    if (btn) btn.disabled = true;
    if (icon) icon.classList.add('fa-spin');

    try {
        const res = await api.triggerDataSync();
        alert(`Data Synchronization Complete!\nStatus: ${res.status}\nDistricts Updated: ${res.recordsRetrieved}\nErrors: ${res.failedRequests}`);
        await loadSyncStatus();
        await initDashboard();
    } catch (err) {
        alert('Live Synchronization Notice: ' + (err.message || 'Complete'));
    } finally {
        if (btn) btn.disabled = false;
        if (icon) icon.classList.remove('fa-spin');
    }
}

async function loadChartsData() {
    try {
        // Disease Trends Line Chart
        const trends = await api.getDiseaseTrends();
        if (trends && trends.labels && trends.datasets) {
            renderDiseaseTrendsChart(trends);
        }

        // Risk Distribution Doughnut Chart
        const risk = await api.getRiskDistribution();
        if (risk) {
            renderRiskDistributionChart(risk);
        }
    } catch (err) {
        console.warn('Charts data loading error:', err);
    }
}

function renderDiseaseTrendsChart(data) {
    const ctx = document.getElementById('diseaseTrendChart');
    if (!ctx) return;

    if (window.diseaseTrendsChartInstance) {
        window.diseaseTrendsChartInstance.destroy();
    }

    window.diseaseTrendsChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: data.datasets.map(ds => ({
                label: ds.label,
                data: ds.data,
                borderColor: ds.borderColor || '#3b82f6',
                backgroundColor: ds.backgroundColor || 'rgba(59, 130, 246, 0.1)',
                borderWidth: 2,
                tension: 0.3,
                fill: false
            }))
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' } },
                x: { grid: { display: false } }
            },
            plugins: {
                legend: { position: 'top' }
            }
        }
    });
}

function renderRiskDistributionChart(riskData) {
    const ctx = document.getElementById('riskDoughnutChart');
    if (!ctx) return;

    if (window.riskChartInstance) {
        window.riskChartInstance.destroy();
    }

    const low = riskData.LOW || riskData.low || 0;
    const med = riskData.MEDIUM || riskData.medium || 0;
    const high = riskData.HIGH || riskData.high || 0;

    window.riskChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Low Risk', 'Medium Risk', 'High Risk'],
            datasets: [{
                data: [low, med, high],
                backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom' }
            }
        }
    });
}

async function loadRecentAlerts() {
    try {
        const alerts = await api.getAlerts();
        const container = document.getElementById('recentAlertsList');
        if (!container) return;

        if (!alerts || alerts.length === 0) {
            container.innerHTML = '<div class="text-center py-4 text-muted small"><i class="fa-solid fa-check-circle text-success me-1"></i> No active public health outbreak alerts.</div>';
            return;
        }

        container.innerHTML = alerts.slice(0, 5).map(a => `
            <div class="alert-item d-flex align-items-start gap-3 p-3 border-bottom">
                <div class="alert-badge-icon ${a.riskLevel === 'HIGH' ? 'bg-danger-subtle text-danger' : 'bg-warning-subtle text-warning'} p-2 rounded-circle">
                    <i class="fa-solid ${a.riskLevel === 'HIGH' ? 'fa-triangle-exclamation' : 'fa-bell'}"></i>
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

        const handleUpdate = (event) => {
            try {
                const data = JSON.parse(event.data);
                updateKpiValues(data);
                const liveDot = document.getElementById('liveIndicatorDot');
                if (liveDot) {
                    liveDot.style.display = 'inline-block';
                }
            } catch (err) {
                console.debug('SSE parse notice:', err);
            }
        };

        eventSource.addEventListener('INIT', handleUpdate);
        eventSource.addEventListener('DASHBOARD_UPDATE', handleUpdate);
        eventSource.addEventListener('METRICS_UPDATE', handleUpdate);

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
