import csv
import random
import math
from datetime import datetime, timedelta

random.seed(42)

districts = [
    {"district": "Chennai", "state": "Tamil Nadu", "pop": 7100000, "density": 26900, "sanitation": 92.5, "lat": 13.0827, "lon": 80.2707},
    {"district": "Madurai", "state": "Tamil Nadu", "pop": 3038000, "density": 820, "sanitation": 81.0, "lat": 9.9252, "lon": 78.1198},
    {"district": "Coimbatore", "state": "Tamil Nadu", "pop": 3458000, "density": 748, "sanitation": 88.5, "lat": 11.0168, "lon": 76.9558},
    {"district": "Tiruchirappalli", "state": "Tamil Nadu", "pop": 2722000, "density": 602, "sanitation": 79.0, "lat": 10.7905, "lon": 78.7047},
    {"district": "Salem", "state": "Tamil Nadu", "pop": 3482000, "density": 665, "sanitation": 75.5, "lat": 11.6643, "lon": 78.1460},
    {"district": "Tirunelveli", "state": "Tamil Nadu", "pop": 1665000, "density": 433, "sanitation": 78.2, "lat": 8.7139, "lon": 77.7567},
    {"district": "Thanjavur", "state": "Tamil Nadu", "pop": 2406000, "density": 705, "sanitation": 72.0, "lat": 10.7870, "lon": 79.1378},
    {"district": "Vellore", "state": "Tamil Nadu", "pop": 1614000, "density": 646, "sanitation": 76.8, "lat": 12.9165, "lon": 79.1325},
    {"district": "Erode", "state": "Tamil Nadu", "pop": 2251000, "density": 392, "sanitation": 84.0, "lat": 11.3410, "lon": 77.7172},
    {"district": "Kanyakumari", "state": "Tamil Nadu", "pop": 1870000, "density": 1119, "sanitation": 91.0, "lat": 8.0883, "lon": 77.5385}
]

# 1. Population CSV
with open("data/population_data.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["district", "state", "population", "population_density", "sanitation_coverage", "latitude", "longitude"])
    for d in districts:
        writer.writerow([d["district"], d["state"], d["pop"], d["density"], d["sanitation"], d["lat"], d["lon"]])

print("Population data generated.")

# 2. Generate time series for 24 bi-weekly dates (1 year: 2025-01-01 to 2025-12-15)
start_date = datetime(2025, 1, 1)
dates = [start_date + timedelta(days=i*15) for i in range(24)]

water_records = []
weather_records = []
disease_records = []
integrated_ml_records = []

for d in districts:
    sanitation = d["sanitation"]
    density = d["density"]
    
    for dt in dates:
        date_str = dt.strftime("%Y-%m-%d")
        month = dt.month
        
        # Monsoon seasonality in Tamil Nadu (Northeast monsoon peak in Oct-Dec, Southwest in Jun-Aug)
        is_monsoon = 1 if month in [10, 11, 12] else (0.5 if month in [6, 7, 8] else 0.1)
        
        # Weather data
        base_temp = 28.0 + 4.0 * math.sin((month - 3) * math.pi / 6)
        temp = round(base_temp + random.uniform(-2.0, 2.0), 1)
        rainfall = round(max(0.0, is_monsoon * random.uniform(20.0, 180.0) + random.uniform(0.0, 15.0)), 1)
        humidity = round(min(98.0, max(45.0, 55.0 + is_monsoon * 30.0 + random.uniform(-5.0, 10.0))), 1)
        weather_records.append([d["district"], d["state"], date_str, temp, rainfall, humidity])
        
        # Water quality
        # Rain causes runoff: higher turbidity, lower DO, varying pH, higher TDS
        turbidity_base = 2.0 + (rainfall / 15.0) + (100 - sanitation) * 0.05
        turbidity = round(max(0.5, turbidity_base + random.uniform(-1.0, 2.5)), 2)
        
        ph_dev = random.uniform(-0.6, 0.6)
        if rainfall > 50:
            ph_dev -= random.uniform(0.2, 0.5)
        ph = round(min(9.0, max(5.5, 7.2 + ph_dev)), 2)
        
        tds = round(200 + (100 - sanitation) * 5 + rainfall * 1.5 + random.uniform(-30, 60), 1)
        dissolved_oxygen = round(max(3.0, min(9.5, 7.5 - (temp - 25) * 0.1 - (turbidity * 0.2) + random.uniform(-0.5, 0.5))), 2)
        water_temp = round(temp - random.uniform(1.0, 2.5), 1)
        water_records.append([d["district"], d["state"], date_str, ph, turbidity, tds, dissolved_oxygen, water_temp])
        
        # Historical disease cases (cholera, typhoid, diarrhea, hepatitis)
        # Risk factors: poor sanitation, high turbidity, high rainfall, low dissolved oxygen, high density
        risk_score = 0.0
        if turbidity > 5.0: risk_score += 1.5
        if turbidity > 8.0: risk_score += 1.5
        if ph < 6.5 or ph > 8.5: risk_score += 1.0
        if dissolved_oxygen < 5.0: risk_score += 1.5
        if rainfall > 60.0: risk_score += 1.5
        if sanitation < 80.0: risk_score += 1.5
        if sanitation < 75.0: risk_score += 1.0
        if density > 1000: risk_score += 1.0
        if density > 10000: risk_score += 1.5
        if temp > 32.0 and humidity > 75.0: risk_score += 1.0
        
        # Generate disease cases
        base_cases = max(0, int(risk_score * 2.5 + random.uniform(-2, 4)))
        cholera = max(0, int(base_cases * 0.15 + (1 if risk_score > 6 else 0)))
        typhoid = max(0, int(base_cases * 0.35 + random.randint(0, 3)))
        diarrhea = max(1, int(base_cases * 1.5 + random.randint(2, 10)))
        hepatitis = max(0, int(base_cases * 0.25 + random.randint(0, 2)))
        
        disease_records.append([d["district"], d["state"], date_str, cholera, typhoid, diarrhea, hepatitis])
        
        # Calculate total disease cases
        total_cases = cholera + typhoid + diarrhea + hepatitis
        
        # Deterministic Risk Labeling based on objective indicator thresholds:
        # HIGH if risk_score >= 6.0 or total_cases >= 25
        # MEDIUM if risk_score >= 3.5 or total_cases >= 12
        # LOW otherwise
        if risk_score >= 6.0 or total_cases >= 25:
            risk_label = "HIGH"
        elif risk_score >= 3.5 or total_cases >= 12:
            risk_label = "MEDIUM"
        else:
            risk_label = "LOW"
            
        integrated_ml_records.append([
            d["district"], ph, turbidity, tds, dissolved_oxygen,
            water_temp, rainfall, humidity, total_cases,
            density, sanitation, risk_label
        ])

with open("data/water_quality.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["district", "state", "date", "ph", "turbidity", "tds", "dissolved_oxygen", "water_temperature"])
    writer.writerows(water_records)

with open("data/weather_data.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["district", "state", "date", "temperature", "rainfall", "humidity"])
    writer.writerows(weather_records)

with open("data/disease_records.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["district", "state", "date", "cholera_cases", "typhoid_cases", "diarrhea_cases", "hepatitis_cases"])
    writer.writerows(disease_records)

print(f"Generated {len(water_records)} records each for water, weather, and disease.")

# Write integrated ML CSV and ARFF for Weka
with open("ml/training/training_dataset.csv", "w", newline="", encoding="utf-8") as f:
    writer = csv.writer(f)
    writer.writerow(["district", "ph", "turbidity", "tds", "dissolved_oxygen", "temperature", "rainfall", "humidity", "previous_disease_cases", "population_density", "sanitation_coverage", "risk_level"])
    writer.writerows(integrated_ml_records)

# Create Weka ARFF
with open("ml/training/training_dataset.arff", "w", encoding="utf-8") as f:
    f.write("@relation aquasafe_disease_risk\n\n")
    f.write("@attribute district {Chennai,Madurai,Coimbatore,Tiruchirappalli,Salem,Tirunelveli,Thanjavur,Vellore,Erode,Kanyakumari}\n")
    f.write("@attribute ph numeric\n")
    f.write("@attribute turbidity numeric\n")
    f.write("@attribute tds numeric\n")
    f.write("@attribute dissolved_oxygen numeric\n")
    f.write("@attribute temperature numeric\n")
    f.write("@attribute rainfall numeric\n")
    f.write("@attribute humidity numeric\n")
    f.write("@attribute previous_disease_cases numeric\n")
    f.write("@attribute population_density numeric\n")
    f.write("@attribute sanitation_coverage numeric\n")
    f.write("@attribute risk_level {LOW,MEDIUM,HIGH}\n\n")
    f.write("@data\n")
    for r in integrated_ml_records:
        f.write(",".join(str(x) for x in r) + "\n")

print("ML Training dataset created (CSV and ARFF).")
