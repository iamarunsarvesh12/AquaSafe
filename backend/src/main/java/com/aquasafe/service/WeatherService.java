package com.aquasafe.service;

import com.aquasafe.entity.PopulationData;
import com.aquasafe.entity.WeatherData;
import com.aquasafe.repository.PopulationDataRepository;
import com.aquasafe.repository.WeatherDataRepository;
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

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherDataRepository weatherRepository;
    private final PopulationDataRepository populationRepository;
    private final RestTemplate restTemplate;

    @Value("${aquasafe.weather.api-url:https://api.open-meteo.com/v1/forecast}")
    private String apiUrl;

    private boolean lastApiCallSuccessful = false;

    public WeatherService(WeatherDataRepository weatherRepository,
                          PopulationDataRepository populationRepository,
                          RestTemplateBuilder restTemplateBuilder) {
        this.weatherRepository = weatherRepository;
        this.populationRepository = populationRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(2000))
                .setReadTimeout(Duration.ofMillis(2000))
                .build();
    }

    /**
     * Gets current weather for a district. Attempts live API first; gracefully falls back to stored data.
     */
    public WeatherData getLatestWeather(String district) {
        Optional<PopulationData> popOpt = populationRepository.findByDistrictIgnoreCase(district);
        if (popOpt.isPresent()) {
            PopulationData pop = popOpt.get();
            try {
                // Query Open-Meteo public free weather API (no key required)
                String url = String.format(Locale.US,
                    "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,precipitation",
                    apiUrl, pop.getLatitude(), pop.getLongitude()
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
                        pop.getDistrict(),
                        pop.getState(),
                        LocalDate.now(),
                        temp,
                        rain,
                        humidity,
                        "LIVE DATA"
                    );
                    log.info("Successfully fetched LIVE weather for {}: {}°C, rain: {}mm", district, temp, rain);
                    return liveData;
                }
            } catch (Exception e) {
                lastApiCallSuccessful = false;
                log.debug("Live weather API unreachable ({}), utilizing latest stored/demo dataset.", e.getMessage());
            }
        }

        // Graceful fallback to stored database record
        Optional<WeatherData> storedOpt = weatherRepository.findTopByDistrictOrderByDateDesc(district);
        if (storedOpt.isPresent()) {
            WeatherData stored = storedOpt.get();
            stored.setSource("STORED DATA");
            return stored;
        }

        // Fallback default if completely empty
        return new WeatherData(district, "Tamil Nadu", LocalDate.now(), 29.5, 12.0, 72.0, "DEMO DATA");
    }

    public List<WeatherData> getAllLatestDistrictsWeather() {
        List<PopulationData> pops = populationRepository.findAll();
        List<WeatherData> result = new ArrayList<>();
        for (PopulationData p : pops) {
            result.add(getLatestWeather(p.getDistrict()));
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

    public boolean isLiveApiOnline() {
        return lastApiCallSuccessful;
    }
}
