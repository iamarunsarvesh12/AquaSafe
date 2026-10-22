/**
 * AquaSafe Alerts Controller
 * Manages public health automated warnings and status updates
 */

document.addEventListener('DOMContentLoaded', () => {
    loadAlertsList();

    const markAllBtn = document.getElementById('btnMarkAllRead');
    if (markAllBtn) {
        markAllBtn.addEventListener('click', async () => {
            try {
                await api.request('/alerts/read-all', { method: 'PUT' });
                await loadAlertsList();
            } catch (err) {
                console.error('Error marking all as read:', err);
            }
        });
    }
});

async function loadAlertsList() {
    const container = document.getElementById('alertsTableBody');
    if (!container) return;

    try {
        const alerts = await api.getAlerts();
        if (!alerts || alerts.length === 0) {
            container.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No active alerts recorded. All monitored districts indicate nominal risk parameters.</td></tr>`;
            return;
        }

        container.innerHTML = alerts.map(a => {
            const isHigh = a.riskLevel === 'HIGH';
            const badgeClass = isHigh ? 'bg-danger' : 'bg-warning text-dark';
            const statusBadge = a.status === 'UNREAD' 
                ? '<span class="badge bg-primary">UNREAD</span>' 
                : '<span class="badge bg-secondary">READ</span>';

            return `
                <tr class="${a.status === 'UNREAD' ? 'table-warning-subtle fw-semibold' : ''}">
                    <td><strong>${escapeHtml(a.district)}</strong></td>
                    <td><span class="badge ${badgeClass}">${a.riskLevel}</span></td>
                    <td>${(a.probability * 100).toFixed(1)}%</td>
                    <td><small class="text-muted">${escapeHtml(a.message)}</small></td>
                    <td>${new Date(a.createdAt).toLocaleString()}</td>
                    <td>${statusBadge}</td>
                    <td>
                        ${a.status === 'UNREAD' ? `
                            <button class="btn btn-sm btn-outline-primary" onclick="markAsRead(${a.id})">
                                <i class="fa-solid fa-check"></i> Mark Read
                            </button>
                        ` : `
                            <button class="btn btn-sm btn-light" disabled>
                                <i class="fa-solid fa-check-double text-success"></i> Read
                            </button>
                        `}
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        console.error('Failed to load alerts:', err);
    }
}

async function markAsRead(id) {
    try {
        await api.markAlertRead(id);
        await loadAlertsList();
    } catch (err) {
        alert('Could not update alert: ' + err.message);
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
