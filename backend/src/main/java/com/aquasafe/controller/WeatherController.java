package com.aquasafe.controller;

import com.aquasafe.entity.WeatherData;
import com.aquasafe.service.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<Page<WeatherData>> getHistoricalWeather(
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(weatherService.getHistoricalWeather(district, page, size));
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherData> getCurrentWeather(@RequestParam(defaultValue = "Chennai") String district) {
        return ResponseEntity.ok(weatherService.getLatestWeather(district));
    }

    @GetMapping("/current/all")
    public ResponseEntity<List<WeatherData>> getAllCurrentWeather() {
        return ResponseEntity.ok(weatherService.getAllLatestDistrictsWeather());
    }
}
