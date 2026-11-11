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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(DataSyncService.class);

    private final DistrictRepository districtRepository;
    private final WeatherRecordRepository weatherRecordRepository;
    private final WeatherDataRepository legacyWeatherRepository;
    private final RestTemplate restTemplate;

    @Value("${aquasafe.weather.api-url:https://api.open-meteo.com/v1/forecast}")
    private String apiUrl;

    private LocalDateTime lastSyncTime = null;
    private String apiConnectionStatus = "STANDBY";
    private int recordsRetrieved = 0;
    private int failedRequests = 0;
    private boolean syncInProgress = false;
    private String lastSyncMessage = "System initialized. Automatic sync scheduled.";

    public DataSyncService(
            DistrictRepository districtRepository,
            WeatherRecordRepository weatherRecordRepository,
            WeatherDataRepository legacyWeatherRepository,
            RestTemplateBuilder restTemplateBuilder) {
        this.districtRepository = districtRepository;
        this.weatherRecordRepository = weatherRecordRepository;
        this.legacyWeatherRepository = legacyWeatherRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(3000))
                .build();
    }

    /**
     * Executes automatic periodic synchronization across all 38 districts.
     * Runs every 5 minutes (300,000 ms) with a 15-second initial delay.
     */
    @Scheduled(initialDelay = 15000, fixedRate = 300000)
    public void scheduledSync() {
        log.info("Starting automatic background data synchronization for Tamil Nadu districts...");
        syncAllDistrictsWeather();
    }

    /**
     * Synchronizes live weather observations for all 38 districts from Open-Meteo.
     * Distinguishes live observations from historical data, prevents duplicate records,
     * and records observation timestamps.
     */
    public synchronized Map<String, Object> syncAllDistrictsWeather() {
        if (syncInProgress) {
            return Map.of(
                "status", "IN_PROGRESS",
                "message", "A data synchronization job is already executing.",
                "lastSyncTime", lastSyncTime != null ? lastSyncTime.toString() : "None"
            );
        }

        syncInProgress = true;
        int successCount = 0;
        int failCount = 0;
        LocalDate today = LocalDate.now();
        String timestampStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        List<District> districts = districtRepository.findAll();
        if (districts.isEmpty()) {
            syncInProgress = false;
            return Map.of("status", "NO_DISTRICTS", "message", "No districts found in master registry.");
        }

        log.info("Synchronizing live weather for {} districts...", districts.size());

        for (District d : districts) {
            try {
                String url = String.format(Locale.US,
                    "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m",
                    apiUrl, d.getLatitude(), d.getLongitude()
                );

                @SuppressWarnings("unchecked")
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                if (response != null && response.containsKey("current")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> current = (Map<String, Object>) response.get("current");

                    double temp = current.containsKey("temperature_2m") && current.get("temperature_2m") != null
                            ? ((Number) current.get("temperature_2m")).doubleValue() : 30.0;
                    double humidity = current.containsKey("relative_humidity_2m") && current.get("relative_humidity_2m") != null
                            ? ((Number) current.get("relative_humidity_2m")).doubleValue() : 70.0;
                    double rain = current.containsKey("precipitation") && current.get("precipitation") != null
                            ? ((Number) current.get("precipitation")).doubleValue() : 0.0;
                    double wind = current.containsKey("wind_speed_10m") && current.get("wind_speed_10m") != null
                            ? ((Number) current.get("wind_speed_10m")).doubleValue() : 12.0;

                    // 1. Update/Save in WeatherRecord (Full 38-District Schema)
                    Optional<WeatherRecord> existingRecord = weatherRecordRepository
                            .findTopByDistrictNameIgnoreCaseAndObservationDate(d.getDistrictName(), today);

                    WeatherRecord wr;
                    if (existingRecord.isPresent()) {
                        wr = existingRecord.get();
                        wr.setTempAvg(temp);
                        wr.setTempMin(temp - 3.5);
                        wr.setTempMax(temp + 3.5);
                        wr.setRainfall(rain);
                        wr.setHumidity(humidity);
                        wr.setWindSpeed(wind);
                        wr.setObservationTimestamp(timestampStr);
                        wr.setSourceAttribution("OPEN_METEO_LIVE_API");
                        wr.setDataType("LIVE_API_OBSERVED");
                    } else {
                        wr = new WeatherRecord(
                            d.getDistrictId(),
                            d.getDistrictCode(),
                            d.getDistrictName(),
                            today,
                            temp - 3.5,
                            temp + 3.5,
                            temp,
                            rain,
                            humidity,
                            wind,
                            "OPEN_METEO_LIVE_API"
                        );
                        wr.setObservationTimestamp(timestampStr);
                        wr.setDataType("LIVE_API_OBSERVED");
                    }
                    weatherRecordRepository.save(wr);

                    // 2. Also keep legacy WeatherData synchronized for backward compatibility
                    Optional<WeatherData> legacyOpt = legacyWeatherRepository
                            .findTopByDistrictOrderByDateDesc(d.getDistrictName());
                    WeatherData legacyW;
                    if (legacyOpt.isPresent() && today.equals(legacyOpt.get().getDate())) {
                        legacyW = legacyOpt.get();
                        legacyW.setTemperature(temp);
                        legacyW.setRainfall(rain);
                        legacyW.setHumidity(humidity);
                        legacyW.setSource("LIVE DATA (Open-Meteo)");
                    } else {
                        legacyW = new WeatherData(
                            d.getDistrictName(),
                            "Tamil Nadu",
                            today,
                            temp,
                            rain,
                            humidity,
                            "LIVE DATA (Open-Meteo)"
                        );
                    }
                    legacyWeatherRepository.save(legacyW);

                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
                log.debug("Failed to fetch live weather for district {}: {}", d.getDistrictName(), e.getMessage());
            }
        }

        this.recordsRetrieved = successCount;
        this.failedRequests = failCount;
        this.lastSyncTime = LocalDateTime.now();
        this.syncInProgress = false;

        if (successCount > 0) {
            this.apiConnectionStatus = "ONLINE";
            this.lastSyncMessage = String.format("Successfully synchronized %d districts at %s. (%d timeouts/fails)",
                successCount, this.lastSyncTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")), failCount);
            log.info(this.lastSyncMessage);
        } else {
            this.apiConnectionStatus = "OFFLINE";
            this.lastSyncMessage = "External weather API unreachable. Graceful fallback active.";
            log.warn("Weather API sync failed for all districts. Fallback active.");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", this.apiConnectionStatus);
        result.put("message", this.lastSyncMessage);
        result.put("recordsRetrieved", this.recordsRetrieved);
        result.put("failedRequests", this.failedRequests);
        result.put("lastSyncTime", this.lastSyncTime != null ? this.lastSyncTime.toString() : null);
        result.put("totalDistricts", districts.size());
        return result;
    }

    public Map<String, Object> getSyncStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("apiConnectionStatus", this.apiConnectionStatus);
        status.put("lastSyncTime", this.lastSyncTime != null ? this.lastSyncTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "Not synchronized yet");
        status.put("recordsRetrieved", this.recordsRetrieved);
        status.put("failedRequests", this.failedRequests);
        status.put("syncInProgress", this.syncInProgress);
        status.put("lastSyncMessage", this.lastSyncMessage);
        status.put("dataSource", "Open-Meteo Historical & Current Weather API");
        status.put("totalMonitoredDistricts", districtRepository.count());
        status.put("liveTrackingActive", "ONLINE".equals(this.apiConnectionStatus));
        return status;
    }

    public boolean isLiveApiOnline() {
        return "ONLINE".equals(this.apiConnectionStatus);
    }

    public LocalDateTime getLastSyncTime() {
        return lastSyncTime;
    }
}

