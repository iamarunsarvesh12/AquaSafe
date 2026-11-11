package com.aquasafe.controller;

import com.aquasafe.entity.WeatherData;
import com.aquasafe.service.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(originPatterns = "*")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherData> getCurrentWeather(@RequestParam(defaultValue = "Chennai") String district) {
        return ResponseEntity.ok(weatherService.getLatestWeather(district));
    }

    @GetMapping("/current/all")
    public ResponseEntity<List<WeatherData>> getAllDistrictsCurrentWeather() {
        return ResponseEntity.ok(weatherService.getAllLatestDistrictsWeather());
    }

    @GetMapping
    public ResponseEntity<Page<WeatherData>> getHistoricalWeather(
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(weatherService.getHistoricalWeather(district, page, size));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getWeatherAnalytics() {
        return ResponseEntity.ok(weatherService.getWeatherAnalytics());
    }

    @GetMapping("/monsoon")
    public ResponseEntity<Map<String, Object>> getMonsoonAnalysis() {
        return ResponseEntity.ok(weatherService.getMonsoonAnalysis());
    }

    @GetMapping("/averages")
    public ResponseEntity<List<Object[]>> getDistrictAverages() {
        return ResponseEntity.ok(weatherService.getDistrictAverages());
    }
}
