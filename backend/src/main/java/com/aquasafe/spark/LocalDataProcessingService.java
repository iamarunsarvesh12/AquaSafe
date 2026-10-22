package com.aquasafe.spark;

import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.WaterQuality;
import com.aquasafe.entity.WeatherData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("localDataProcessingService")
public class LocalDataProcessingService implements DataProcessingService {

    @Override
    public DataProcessingResult processAggregates(List<WaterQuality> wqList, List<WeatherData> weatherList, List<DiseaseRecord> diseaseList) {
        long startTime = System.currentTimeMillis();

        DataProcessingResult result = new DataProcessingResult();
        result.setEngineName(getEngineName());

        int totalRecords = wqList.size() + weatherList.size() + diseaseList.size();
        result.setRecordsProcessed(totalRecords);
        result.setDatasetSize(totalRecords * 128L); // estimated byte size

        // 1. Distinct districts
        Set<String> districts = new HashSet<>();
        wqList.forEach(w -> districts.add(w.getDistrict()));
        weatherList.forEach(w -> districts.add(w.getDistrict()));
        diseaseList.forEach(d -> districts.add(d.getDistrict()));
        result.setDistrictCount(districts.size());

        // 2. Water Quality Averages
        Map<String, Double> wqAvg = new LinkedHashMap<>();
        if (!wqList.isEmpty()) {
            wqAvg.put("avgPh", round(wqList.stream().mapToDouble(WaterQuality::getPh).average().orElse(0.0)));
            wqAvg.put("avgTurbidity", round(wqList.stream().mapToDouble(WaterQuality::getTurbidity).average().orElse(0.0)));
            wqAvg.put("avgTds", round(wqList.stream().mapToDouble(WaterQuality::getTds).average().orElse(0.0)));
            wqAvg.put("avgDissolvedOxygen", round(wqList.stream().mapToDouble(WaterQuality::getDissolvedOxygen).average().orElse(0.0)));
            wqAvg.put("avgWaterTemperature", round(wqList.stream().mapToDouble(WaterQuality::getWaterTemperature).average().orElse(0.0)));
        }
        result.setWaterQualityAverages(wqAvg);

        // 3. Weather Averages
        Map<String, Double> weatherAvg = new LinkedHashMap<>();
        if (!weatherList.isEmpty()) {
            weatherAvg.put("avgTemperature", round(weatherList.stream().mapToDouble(WeatherData::getTemperature).average().orElse(0.0)));
            weatherAvg.put("avgRainfall", round(weatherList.stream().mapToDouble(WeatherData::getRainfall).average().orElse(0.0)));
            weatherAvg.put("avgHumidity", round(weatherList.stream().mapToDouble(WeatherData::getHumidity).average().orElse(0.0)));
        }
        result.setWeatherAverages(weatherAvg);

        // 4. Monthly Disease Trends
        Map<String, Long> monthlyTrends = diseaseList.stream()
            .collect(Collectors.groupingBy(
                d -> d.getDate().getYear() + "-" + String.format("%02d", d.getDate().getMonthValue()),
                TreeMap::new,
                Collectors.summingLong(d -> (long) d.getTotalCases())
            ));
        result.setMonthlyDiseaseTrends(monthlyTrends);

        // 5. District Aggregates
        Map<String, Map<String, Object>> districtMap = new LinkedHashMap<>();
        for (String district : districts) {
            Map<String, Object> dStats = new LinkedHashMap<>();

            // Disease stats
            long dCases = diseaseList.stream()
                .filter(d -> d.getDistrict().equalsIgnoreCase(district))
                .mapToLong(DiseaseRecord::getTotalCases)
                .sum();
            dStats.put("totalDiseaseCases", dCases);

            // Water stats
            OptionalDouble avgTurb = wqList.stream()
                .filter(w -> w.getDistrict().equalsIgnoreCase(district))
                .mapToDouble(WaterQuality::getTurbidity)
                .average();
            dStats.put("avgTurbidity", avgTurb.isPresent() ? round(avgTurb.getAsDouble()) : 0.0);

            // Rain stats
            OptionalDouble avgRain = weatherList.stream()
                .filter(w -> w.getDistrict().equalsIgnoreCase(district))
                .mapToDouble(WeatherData::getRainfall)
                .average();
            dStats.put("avgRainfall", avgRain.isPresent() ? round(avgRain.getAsDouble()) : 0.0);

            districtMap.put(district, dStats);
        }
        result.setDistrictAggregates(districtMap);

        long duration = System.currentTimeMillis() - startTime;
        result.setProcessingTimeMs(Math.max(1, duration));
        result.setStatus("SUCCESS");

        return result;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    @Override
    public String getEngineName() {
        return "Local In-Memory Analytics Engine (Java Stream Pipeline)";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
