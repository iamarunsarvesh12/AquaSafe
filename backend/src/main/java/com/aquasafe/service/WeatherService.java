package com.aquasafe.service;

import com.aquasafe.entity.District;
import com.aquasafe.entity.WeatherData;
import com.aquasafe.entity.WeatherRecord;
import com.aquasafe.repository.DistrictRepository;
import com.aquasafe.repository.WeatherDataRepository;
import com.aquasafe.repository.WeatherRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherDataRepository weatherRepository;
    private final WeatherRecordRepository weatherRecordRepository;
    private final DistrictRepository districtRepository;
    private final RestTemplate restTemplate;

    @Value("${aquasafe.weather.api-url:https://api.open-meteo.com/v1/forecast}")
    private String apiUrl;

    private boolean lastApiCallSuccessful = false;

    public WeatherService(WeatherDataRepository weatherRepository,
                          WeatherRecordRepository weatherRecordRepository,
                          DistrictRepository districtRepository,
                          RestTemplateBuilder restTemplateBuilder) {
        this.weatherRepository = weatherRepository;
        this.weatherRecordRepository = weatherRecordRepository;
        this.districtRepository = districtRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(2500))
                .setReadTimeout(Duration.ofMillis(2500))
                .build();
    }

    public WeatherData getLatestWeather(String district) {
        Optional<District> distOpt = districtRepository.findByDistrictNameIgnoreCase(district);
        if (distOpt.isPresent()) {
            District dist = distOpt.get();
            try {
                String url = String.format(Locale.US,
                    "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,precipitation",
                    apiUrl, dist.getLatitude(), dist.getLongitude()
                );
                @SuppressWarnings("unchecked")
                Map<String, Object> resp = restTemplate.getForObject(url, Map.class);
                if (resp != null && resp.containsKey("current")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> current = (Map<String, Object>) resp.get("current");
                    double temp = ((Number) current.get("temperature_2m")).doubleValue();
                    double humidity = ((Number) current.get("relative_humidity_2m")).doubleValue();
                    double rain = ((Number) current.get("precipitation")).doubleValue();

                    lastApiCallSuccessful = true;
                    WeatherData liveData = new WeatherData(
                        dist.getDistrictName(),
                        "Tamil Nadu",
                        LocalDate.now(),
                        temp,
                        rain,
                        humidity,
                        "LIVE DATA (Open-Meteo API)"
                    );
                    log.debug("Successfully fetched LIVE weather for {}: {}°C, rain: {}mm", district, temp, rain);
                    return liveData;
                }
            } catch (Exception e) {
                lastApiCallSuccessful = false;
                log.debug("Live weather API unreachable for {} ({}), utilizing latest stored dataset.", district, e.getMessage());
            }
        }

        // Fallback to latest stored record in WeatherRecord or WeatherData
        Optional<WeatherRecord> wrOpt = weatherRecordRepository.findTopByDistrictNameIgnoreCaseOrderByObservationDateDesc(district);
        if (wrOpt.isPresent()) {
            WeatherRecord wr = wrOpt.get();
            return new WeatherData(
                wr.getDistrictName(),
                "Tamil Nadu",
                wr.getObservationDate(),
                wr.getTempAvg(),
                wr.getRainfall(),
                wr.getHumidity(),
                "STORED DATA (" + wr.getSourceAttribution() + ")"
            );
        }

        Optional<WeatherData> storedOpt = weatherRepository.findTopByDistrictOrderByDateDesc(district);
        if (storedOpt.isPresent()) {
            WeatherData stored = storedOpt.get();
            stored.setSource("STORED DATA (IMD Gridded)");
            return stored;
        }

        return new WeatherData(district, "Tamil Nadu", LocalDate.now(), 29.5, 12.0, 72.0, "STORED DATA (Baseline)");
    }

    public List<WeatherData> getAllLatestDistrictsWeather() {
        List<District> districts = districtRepository.findAll();
        List<WeatherData> result = new ArrayList<>();
        for (District d : districts) {
            result.add(getLatestWeather(d.getDistrictName()));
        }
        return result;
    }

    public Page<WeatherData> getHistoricalWeather(String district, int page, int size) {
        if (district != null && !district.trim().isEmpty()) {
            return weatherRepository.findByDistrictContainingIgnoreCase(district, PageRequest.of(page, size));
        }
        return weatherRepository.findAll(PageRequest.of(page, size));
    }

    public List<Object[]> getDistrictAverages() {
        return weatherRepository.getDistrictAverages();
    }

    public Map<String, Object> getWeatherAnalytics() {
        List<WeatherData> all = weatherRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        if (all.isEmpty()) {
            result.put("totalRecords", 0);
            return result;
        }

        double avgTemp = all.stream().mapToDouble(WeatherData::getTemperature).average().orElse(28.0);
        double avgRain = all.stream().mapToDouble(WeatherData::getRainfall).average().orElse(50.0);
        double maxRain = all.stream().mapToDouble(WeatherData::getRainfall).max().orElse(150.0);
        double avgHum = all.stream().mapToDouble(WeatherData::getHumidity).average().orElse(65.0);

        result.put("totalObservations", all.size());
        result.put("averageTemperature", Math.round(avgTemp * 10.0) / 10.0);
        result.put("averageRainfall", Math.round(avgRain * 10.0) / 10.0);
        result.put("maximumRainfall", Math.round(maxRain * 10.0) / 10.0);
        result.put("averageHumidity", Math.round(avgHum * 10.0) / 10.0);

        // District-wise aggregation
        Map<String, List<WeatherData>> byDist = all.stream().collect(Collectors.groupingBy(WeatherData::getDistrict));
        List<Map<String, Object>> distList = new ArrayList<>();

        for (Map.Entry<String, List<WeatherData>> entry : byDist.entrySet()) {
            String dist = entry.getKey();
            List<WeatherData> records = entry.getValue();

            double dTemp = records.stream().mapToDouble(WeatherData::getTemperature).average().orElse(0.0);
            double dRainAvg = records.stream().mapToDouble(WeatherData::getRainfall).average().orElse(0.0);
            double dRainTotal = records.stream().mapToDouble(WeatherData::getRainfall).sum();
            double dHum = records.stream().mapToDouble(WeatherData::getHumidity).average().orElse(0.0);

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("district", dist);
            m.put("avgTemperature", Math.round(dTemp * 10.0) / 10.0);
            m.put("avgRainfall", Math.round(dRainAvg * 10.0) / 10.0);
            m.put("totalRainfall", Math.round(dRainTotal * 10.0) / 10.0);
            m.put("avgHumidity", Math.round(dHum * 10.0) / 10.0);
            distList.add(m);
        }

        distList.sort((a, b) -> Double.compare((Double) b.get("totalRainfall"), (Double) a.get("totalRainfall")));
        result.put("districtWeather", distList);

        return result;
    }

    public Map<String, Object> getMonsoonAnalysis() {
        List<WeatherData> all = weatherRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        double neRain = 0, swRain = 0, summerRain = 0, winterRain = 0;
        int neCount = 0, swCount = 0, summerCount = 0, winterCount = 0;

        for (WeatherData w : all) {
            int m = w.getDate().getMonthValue();
            double r = w.getRainfall();
            if (m == 10 || m == 11 || m == 12) {
                neRain += r;
                neCount++;
            } else if (m >= 6 && m <= 9) {
                swRain += r;
                swCount++;
            } else if (m >= 3 && m <= 5) {
                summerRain += r;
                summerCount++;
            } else {
                winterRain += r;
                winterCount++;
            }
        }

        Map<String, Object> seasons = new LinkedHashMap<>();
        seasons.put("Northeast Monsoon (Oct-Dec)", Map.of(
            "avgRainfall", neCount > 0 ? Math.round((neRain / neCount) * 10.0) / 10.0 : 0,
            "totalObservations", neCount,
            "riskImpact", "HIGHEST: Surface runoff and coastal/urban water contamination"
        ));
        seasons.put("Southwest Monsoon (Jun-Sep)", Map.of(
            "avgRainfall", swCount > 0 ? Math.round((swRain / swCount) * 10.0) / 10.0 : 0,
            "totalObservations", swCount,
            "riskImpact", "MODERATE: Western Ghats catchment recharge"
        ));
        seasons.put("Summer Pre-Monsoon (Mar-May)", Map.of(
            "avgRainfall", summerCount > 0 ? Math.round((summerRain / summerCount) * 10.0) / 10.0 : 0,
            "totalObservations", summerCount,
            "riskImpact", "LOW: Water scarcity and TDS concentration"
        ));
        seasons.put("Winter (Jan-Feb)", Map.of(
            "avgRainfall", winterCount > 0 ? Math.round((winterRain / winterCount) * 10.0) / 10.0 : 0,
            "totalObservations", winterCount,
            "riskImpact", "NOMINAL: Baseline epidemiological surveillance"
        ));

        result.put("state", "Tamil Nadu");
        result.put("monsoonSeasons", seasons);
        return result;
    }

    public boolean isLiveApiOnline() {
        return lastApiCallSuccessful;
    }
}
