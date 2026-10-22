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
        return user ? JSON.parse(user) : { username: 'Analyst', role: 'USER' };
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

    // 2. Water Quality
    async getWaterQuality(district = '', page = 0, size = 15) {
        const query = `?district=${encodeURIComponent(district)}&page=${page}&size=${size}`;
        return this.request(`/water-quality${query}`);
    },

    // 3. Weather Data
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

    // 4. Disease Surveillance
    async getDiseases(district = '', page = 0, size = 15) {
        const query = `?district=${encodeURIComponent(district)}&page=${page}&size=${size}`;
        return this.request(`/diseases${query}`);
    },

    // 5. Population & Geo
    async getPopulation() {
        return this.request('/population');
    },

    // 6. Datasets & Upload
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

    // 7. Preprocessing
    async runPreprocessing() {
        return this.request('/preprocessing/run', { method: 'POST' });
    },

    // 8. ML Model Management
    async trainModel() {
        return this.request('/model/train', { method: 'POST' });
    },

    async getModelEvaluation() {
        return this.request('/model/evaluation');
    },

    // 9. Prediction Engine
    async predictRisk(params) {
        return this.request('/predictions/predict', {
            method: 'POST',
            body: JSON.stringify(params)
        });
    },

    async getPredictionHistory() {
        return this.request('/predictions/history');
    },

    // 10. Alerts
    async getAlerts() {
        return this.request('/alerts');
    },

    async markAlertRead(id) {
        return this.request(`/alerts/${id}/read`, { method: 'PUT' });
    },

    // 11. Reports
    async getReports(district = '', startDate = '', endDate = '') {
        let query = '?';
        if (district) query += `district=${encodeURIComponent(district)}&`;
        if (startDate) query += `startDate=${startDate}&`;
        if (endDate) query += `endDate=${endDate}&`;
        return this.request(`/reports${query}`);
    },

    // 12. System & Big Data Processing
    async getSystemHealth() {
        return this.request('/system/health');
    },

    async triggerBigDataProcessing() {
        return this.request('/system/spark/process', { method: 'POST' });
    },

    // 13. Auth
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
                    : (user.username.toLowerCase() === 'analyst' ? 'Public Health Analyst' : user.username);
            }
            const roleEl = document.getElementById('navUserRole');
            if (roleEl && user && user.role) {
                roleEl.textContent = `${user.role.toUpperCase()} ROLE`;
            }

            // Sync alert notification badge in navigation
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

// Automatically bind session indicators on every page
document.addEventListener('DOMContentLoaded', () => {
    api.initAuthUI();
});

