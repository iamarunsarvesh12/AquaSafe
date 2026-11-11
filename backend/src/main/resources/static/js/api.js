/**
 * AquaSafe REST API Client Module
 * Communicates with the Spring Boot backend service
 */

const API_BASE = window.location.origin.includes('8080') 
    ? window.location.origin + '/api' 
    : 'http://localhost:8080/api';

const api = {
    // Auth Helpers
    getToken() {
        return localStorage.getItem('aquasafe_token');
    },

    getUser() {
        const user = localStorage.getItem('aquasafe_user');
        return user ? JSON.parse(user) : { username: 'analyst', role: 'USER' };
    },

    setUser(userData) {
        localStorage.setItem('aquasafe_user', JSON.stringify(userData));
        if (userData.token) {
            localStorage.setItem('aquasafe_token', userData.token);
        }
    },

    logout() {
        localStorage.removeItem('aquasafe_token');
        localStorage.removeItem('aquasafe_user');
        window.location.href = 'login.html';
    },

    async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const headers = options.headers || {};

        if (!(options.body instanceof FormData)) {
            headers['Content-Type'] = 'application/json';
        }

        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            if (!response.ok) {
                let errData;
                try {
                    errData = await response.json();
                } catch {
                    errData = { message: `HTTP Error ${response.status}: ${response.statusText}` };
                }
                throw new Error(errData.message || 'Server request failed');
            }

            const contentType = response.headers.get('content-type') || '';
            if (contentType.includes('application/json')) {
                return await response.json();
            } else {
                const text = await response.text();
                return { message: text, success: true };
            }
        } catch (err) {
            console.error(`API Error [${endpoint}]:`, err.message);
            throw err;
        }
    },

    // 1. Dashboard Endpoints
    async getDashboardSummary() {
        return this.request('/dashboard/summary');
    },

    async getDiseaseTrends() {
        return this.request('/dashboard/disease-trends');
    },

    async getWaterQualityTrends() {
        return this.request('/dashboard/water-quality');
    },

    async getRiskDistribution() {
        return this.request('/dashboard/risk-distribution');
    },

    async getDistrictRisk() {
        return this.request('/dashboard/district-risk');
    },

    // 2. Districts Master (All 38 Districts)
    async getDistricts() {
        return this.request('/districts');
    },

    async getDistrictByName(name) {
        return this.request(`/districts/${encodeURIComponent(name)}`);
    },

    async getDistrictsSummary() {
        return this.request('/districts/summary');
    },

    // 3. Water Quality Analytics
    async getWaterQuality(district = '', page = 0, size = 15) {
        const query = `?district=${encodeURIComponent(district)}&page=${page}&size=${size}`;
        return this.request(`/water-quality${query}`);
    },

    async getWaterQualityAnalytics() {
        return this.request('/water-quality/analytics');
    },

    async getWaterQualityViolations() {
        return this.request('/water-quality/violations');
    },

    // 4. Weather Data Analytics
    async getCurrentWeather(district = 'Chennai') {
        return this.request(`/weather/current?district=${encodeURIComponent(district)}`);
    },

    async getCurrentWeatherAll() {
        return this.request('/weather/current/all');
    },

    async getHistoricalWeather(district = '', page = 0, size = 15) {
        const query = `?district=${encodeURIComponent(district)}&page=${page}&size=${size}`;
        return this.request(`/weather${query}`);
    },

    async getWeatherAnalytics() {
        return this.request('/weather/analytics');
    },

    async getMonsoonAnalysis() {
        return this.request('/weather/monsoon');
    },

    // 5. Disease Surveillance
    async getDiseases(district = '', page = 0, size = 15) {
        const query = `?district=${encodeURIComponent(district)}&page=${page}&size=${size}`;
        return this.request(`/diseases${query}`);
    },

    async getDiseaseAnalytics() {
        return this.request('/diseases/analytics');
    },

    async getDistrictIncidence() {
        return this.request('/diseases/incidence');
    },

    // 6. Disease Forecasting
    async getForecasts(district = '') {
        const query = district ? `?district=${encodeURIComponent(district)}` : '';
        return this.request(`/forecasts${query}`);
    },

    async getForecastMetrics() {
        return this.request('/forecasts/metrics');
    },

    // 7. Population & Geo
    async getPopulation() {
        return this.request('/population');
    },

    // 8. Datasets, Sources & Upload
    async uploadDataset(formData) {
        const url = `${API_BASE}/datasets/upload`;
        const resp = await fetch(url, {
            method: 'POST',
            body: formData
        });
        if (!resp.ok) {
            const err = await resp.json();
            throw new Error(err.message || 'Dataset upload failed');
        }
        return await resp.json();
    },

    async getImportHistory() {
        return this.request('/datasets');
    },

    async getDatasetSources() {
        return this.request('/datasets/sources');
    },

    async getDataQualityReports() {
        return this.request('/datasets/quality');
    },

    // 9. Preprocessing
    async runPreprocessing() {
        return this.request('/preprocessing/run', { method: 'POST' });
    },

    // 10. ML Model Management
    async trainModel() {
        return this.request('/model/train', { method: 'POST' });
    },

    async getModelEvaluation() {
        return this.request('/model/evaluation');
    },

    async getModelComparison() {
        return this.request('/model/comparison');
    },

    // 11. Prediction Engine
    async predictRisk(params) {
        return this.request('/predictions/predict', {
            method: 'POST',
            body: JSON.stringify(params)
        });
    },

    async getPredictionHistory() {
        return this.request('/predictions/history');
    },

    // 12. Alerts
    async getAlerts() {
        return this.request('/alerts');
    },

    async markAlertRead(id) {
        return this.request(`/alerts/${id}/read`, { method: 'PUT' });
    },

    async markAllAlertsRead() {
        return this.request('/alerts/read-all', { method: 'PUT' });
    },

    // 13. Reports
    async getReports(district = '', startDate = '', endDate = '') {
        let query = '?';
        if (district) query += `district=${encodeURIComponent(district)}&`;
        if (startDate) query += `startDate=${startDate}&`;
        if (endDate) query += `endDate=${endDate}&`;
        return this.request(`/reports${query}`);
    },

    // 14. System & Big Data Processing
    async getSystemHealth() {
        return this.request('/system/health');
    },

    async triggerBigDataProcessing() {
        return this.request('/system/spark/process', { method: 'POST' });
    },

    async getSparkHistory() {
        return this.request('/system/spark/history');
    },

    async getSparkReport() {
        return this.request('/system/spark/report');
    },

    // 15. User Management
    async getUsers() {
        return this.request('/users');
    },

    async createUser(payload) {
        return this.request('/users', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
    },

    async deleteUser(id) {
        return this.request(`/users/${id}`, {
            method: 'DELETE'
        });
    },

    async toggleUserStatus(id) {
        return this.request(`/users/${id}/toggle-status`, {
            method: 'PUT'
        });
    },

    // 16. Live Data Synchronization
    async triggerDataSync() {
        return this.request('/system/sync', {
            method: 'POST'
        });
    },

    async getDataSyncStatus() {
        return this.request('/system/sync-status');
    },

    // 17. Auth
    async login(username, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    },

    // Session UI Sync Helper
    initAuthUI() {
        try {
            const user = this.getUser();
            const nameEl = document.getElementById('navUsername');
            if (nameEl && user && user.username) {
                nameEl.textContent = user.username.toLowerCase() === 'admin' 
                    ? 'System Administrator' 
                    : (user.username.toLowerCase() === 'dho' ? 'District Health Officer' : 'Public Health Analyst');
            }
            const roleEl = document.getElementById('navUserRole');
            if (roleEl && user && user.role) {
                roleEl.textContent = `${user.role.toUpperCase()} ROLE`;
            }

            // Sync alert badge
            this.getAlerts().then(alerts => {
                if (!alerts) return;
                const unreadCount = alerts.filter(a => a.status === 'UNREAD').length;
                const sidebarBadge = document.getElementById('sidebarAlertBadge');
                if (sidebarBadge) {
                    sidebarBadge.textContent = unreadCount;
                    sidebarBadge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
                }
                const navBadge = document.getElementById('navAlertCountBadge');
                if (navBadge) {
                    navBadge.textContent = unreadCount > 0 ? unreadCount : '!';
                    navBadge.style.display = unreadCount > 0 ? 'inline-block' : 'none';
                }
            }).catch(() => {});
        } catch (e) {
            console.debug('AquaSafe Auth UI init notice:', e);
        }
    }
};

window.api = api;

document.addEventListener('DOMContentLoaded', () => {
    api.initAuthUI();
});
