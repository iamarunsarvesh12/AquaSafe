/**
 * AquaSafe Chart.js Visualizations
 * Dynamic, responsive healthcare intelligence charts
 */

const AquaCharts = {
    diseaseChart: null,
    riskChart: null,
    districtChart: null,
    scatterChart: null,

    initDiseaseTrend(canvasId, trendData) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        if (this.diseaseChart) {
            this.diseaseChart.destroy();
        }

        this.diseaseChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: trendData.labels || [],
                datasets: [
                    {
                        label: 'Diarrhea',
                        data: trendData.diarrhea || [],
                        borderColor: '#0284c7',
                        backgroundColor: 'rgba(2, 132, 199, 0.08)',
                        fill: true,
                        tension: 0.35,
                        borderWidth: 2
                    },
                    {
                        label: 'Typhoid',
                        data: trendData.typhoid || [],
                        borderColor: '#f59e0b',
                        backgroundColor: 'transparent',
                        tension: 0.35,
                        borderWidth: 2
                    },
                    {
                        label: 'Hepatitis',
                        data: trendData.hepatitis || [],
                        borderColor: '#8b5cf6',
                        backgroundColor: 'transparent',
                        tension: 0.35,
                        borderWidth: 2
                    },
                    {
                        label: 'Cholera',
                        data: trendData.cholera || [],
                        borderColor: '#ef4444',
                        backgroundColor: 'transparent',
                        tension: 0.35,
                        borderWidth: 2
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                        labels: { boxWidth: 12, font: { family: 'Inter', size: 12 } }
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { font: { family: 'Inter', size: 11 } }
                    },
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: 'Reported Cases', font: { family: 'Inter', size: 11 } },
                        grid: { color: '#f1f5f9' }
                    }
                }
            }
        });
    },

    initRiskDistribution(canvasId, distData) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        if (this.riskChart) {
            this.riskChart.destroy();
        }

        this.riskChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: distData.labels || ['LOW', 'MEDIUM', 'HIGH'],
                datasets: [{
                    data: distData.data || [0, 0, 0],
                    backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
                    borderWidth: 3,
                    borderColor: '#ffffff',
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { boxWidth: 12, font: { family: 'Inter', size: 12 } }
                    }
                },
                cutout: '72%'
            }
        });
    },

    initDistrictRiskBar(canvasId, districtData) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        if (this.districtChart) {
            this.districtChart.destroy();
        }

        this.districtChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: districtData.districts || [],
                datasets: [
                    {
                        label: 'Avg Turbidity (NTU)',
                        data: districtData.turbidity || [],
                        backgroundColor: 'rgba(2, 132, 199, 0.8)',
                        borderRadius: 6
                    },
                    {
                        label: 'Dissolved Oxygen (mg/L)',
                        data: districtData.dissolvedOxygen || [],
                        backgroundColor: 'rgba(13, 148, 136, 0.8)',
                        borderRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                        labels: { boxWidth: 12, font: { family: 'Inter', size: 12 } }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { font: { family: 'Inter', size: 11 } }
                    },
                    y: {
                        beginAtZero: true,
                        grid: { color: '#f1f5f9' }
                    }
                }
            }
        });
    },

    initScatterCorrelation(canvasId) {
        const ctx = document.getElementById(canvasId);
        if (!ctx) return;

        // Realistic scatter points: Rainfall (mm) vs Disease Cases
        const points = [
            { x: 12, y: 14 }, { x: 25, y: 19 }, { x: 38, y: 22 }, { x: 50, y: 31 },
            { x: 75, y: 45 }, { x: 92, y: 58 }, { x: 115, y: 64 }, { x: 140, y: 79 },
            { x: 160, y: 88 }, { x: 30, y: 18 }, { x: 45, y: 27 }, { x: 80, y: 52 },
            { x: 105, y: 61 }, { x: 130, y: 72 }, { x: 155, y: 85 }
        ];

        if (this.scatterChart) {
            this.scatterChart.destroy();
        }

        this.scatterChart = new Chart(ctx, {
            type: 'scatter',
            data: {
                datasets: [{
                    label: 'Rainfall vs Total Disease Burden',
                    data: points,
                    backgroundColor: '#0284c7',
                    pointRadius: 6,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: (ctx) => `Rainfall: ${ctx.parsed.x} mm, Cases: ${ctx.parsed.y}`
                        }
                    }
                },
                scales: {
                    x: {
                        title: { display: true, text: 'Rainfall (mm)', font: { family: 'Inter', size: 11 } },
                        grid: { color: '#f1f5f9' }
                    },
                    y: {
                        title: { display: true, text: 'Recorded Disease Cases', font: { family: 'Inter', size: 11 } },
                        grid: { color: '#f1f5f9' }
                    }
                }
            }
        });
    }
};

window.AquaCharts = AquaCharts;
