package com.aquasafe.service;

import com.aquasafe.config.FilePathResolver;
import com.aquasafe.dto.PreprocessingStatsDTO;
import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.PopulationData;
import com.aquasafe.entity.WaterQuality;
import com.aquasafe.entity.WeatherData;
import com.aquasafe.repository.DiseaseRecordRepository;
import com.aquasafe.repository.PopulationDataRepository;
import com.aquasafe.repository.WaterQualityRepository;
import com.aquasafe.repository.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

@Service
public class DataPreprocessingService {

    private static final Logger log = LoggerFactory.getLogger(DataPreprocessingService.class);

    private final WaterQualityRepository waterQualityRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final DiseaseRecordRepository diseaseRecordRepository;
    private final PopulationDataRepository populationDataRepository;

    public DataPreprocessingService(
            WaterQualityRepository waterQualityRepository,
            WeatherDataRepository weatherDataRepository,
            DiseaseRecordRepository diseaseRecordRepository,
            PopulationDataRepository populationDataRepository) {
        this.waterQualityRepository = waterQualityRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.diseaseRecordRepository = diseaseRecordRepository;
        this.populationDataRepository = populationDataRepository;
    }

    public PreprocessingStatsDTO cleanAndIntegrateData() {
        log.info("Starting data cleaning, validation, deduplication, and integration pipeline...");

        List<WaterQuality> rawWq = waterQualityRepository.findAll();
        List<WeatherData> rawWeather = weatherDataRepository.findAll();
        List<DiseaseRecord> rawDisease = diseaseRecordRepository.findAll();

        int recordsBefore = rawWq.size() + rawWeather.size() + rawDisease.size();
        int missingHandled = 0;
        int duplicatesRemoved = 0;
        int invalidFiltered = 0;

        // 1. Deduplicate & Clean Water Quality
        Map<String, WaterQuality> cleanWqMap = new LinkedHashMap<>();
        for (WaterQuality w : rawWq) {
            String key = w.getDistrict().toLowerCase() + "_" + w.getDate();
            if (cleanWqMap.containsKey(key)) {
                duplicatesRemoved++;
                continue;
            }
            if (w.getPh() == null || w.getPh() < 0.0 || w.getPh() > 14.0 ||
                w.getTurbidity() == null || w.getTurbidity() < 0.0 ||
                w.getTds() == null || w.getTds() < 0.0 ||
                w.getDissolvedOxygen() == null || w.getDissolvedOxygen() < 0.0) {
                invalidFiltered++;
                continue;
            }
            cleanWqMap.put(key, w);
        }

        // 2. Deduplicate & Clean Weather
        Map<String, WeatherData> cleanWeatherMap = new LinkedHashMap<>();
        for (WeatherData w : rawWeather) {
            String key = w.getDistrict().toLowerCase() + "_" + w.getDate();
            if (cleanWeatherMap.containsKey(key)) {
                duplicatesRemoved++;
                continue;
            }
            if (w.getTemperature() == null || w.getRainfall() == null || w.getRainfall() < 0.0 ||
                w.getHumidity() == null || w.getHumidity() < 0.0 || w.getHumidity() > 100.0) {
                invalidFiltered++;
                continue;
            }
            cleanWeatherMap.put(key, w);
        }

        // 3. Deduplicate & Clean Disease Records
        Map<String, DiseaseRecord> cleanDiseaseMap = new LinkedHashMap<>();
        for (DiseaseRecord d : rawDisease) {
            String key = d.getDistrict().toLowerCase() + "_" + d.getDate();
            if (cleanDiseaseMap.containsKey(key)) {
                duplicatesRemoved++;
                continue;
            }
            if (d.getCholeraCases() == null || d.getCholeraCases() < 0 ||
                d.getTyphoidCases() == null || d.getTyphoidCases() < 0 ||
                d.getDiarrheaCases() == null || d.getDiarrheaCases() < 0 ||
                d.getHepatitisCases() == null || d.getHepatitisCases() < 0) {
                invalidFiltered++;
                continue;
            }
            cleanDiseaseMap.put(key, d);
        }

        // 4. Integrate Datasets
        List<PopulationData> populationList = populationDataRepository.findAll();
        Map<String, PopulationData> popMap = new HashMap<>();
        for (PopulationData p : populationList) {
            popMap.put(p.getDistrict().toLowerCase(), p);
        }

        List<String[]> integratedRows = new ArrayList<>();
        Set<String> allKeys = new HashSet<>(cleanWqMap.keySet());
        allKeys.addAll(cleanWeatherMap.keySet());
        allKeys.addAll(cleanDiseaseMap.keySet());

        for (String key : allKeys) {
            WaterQuality wq = cleanWqMap.get(key);
            WeatherData weather = cleanWeatherMap.get(key);
            DiseaseRecord disease = cleanDiseaseMap.get(key);

            if (wq == null || weather == null || disease == null) {
                missingHandled++;
                continue;
            }

            String district = wq.getDistrict();
            PopulationData pop = popMap.get(district.toLowerCase());
            int density = pop != null ? pop.getPopulationDensity() : 600;
            double sanitation = pop != null ? pop.getSanitationCoverage() : 80.0;

            int prevCases = disease.getTotalCases();

            String riskLabel = determineRiskLabel(
                wq.getPh(), wq.getTurbidity(), wq.getDissolvedOxygen(),
                weather.getRainfall(), prevCases, density, sanitation
            );

            integratedRows.add(new String[]{
                district,
                String.format(Locale.US, "%.2f", wq.getPh()),
                String.format(Locale.US, "%.2f", wq.getTurbidity()),
                String.format(Locale.US, "%.1f", wq.getTds()),
                String.format(Locale.US, "%.2f", wq.getDissolvedOxygen()),
                String.format(Locale.US, "%.1f", weather.getTemperature()),
                String.format(Locale.US, "%.1f", weather.getRainfall()),
                String.format(Locale.US, "%.1f", weather.getHumidity()),
                String.valueOf(prevCases),
                String.valueOf(density),
                String.format(Locale.US, "%.1f", sanitation),
                riskLabel
            });
        }

        int recordsAfterCleaning = cleanWqMap.size() + cleanWeatherMap.size() + cleanDiseaseMap.size();

        if (!integratedRows.isEmpty()) {
            writeArffFile("ml/training/training_dataset.arff", integratedRows);
        }

        log.info("Preprocessing complete: Before={}, After={}, MissingHandled={}, DuplicatesRemoved={}, InvalidFiltered={}",
                recordsBefore, recordsAfterCleaning, missingHandled, duplicatesRemoved, invalidFiltered);

        return new PreprocessingStatsDTO(
            "Integrated Multi-Source (Water Quality, Weather, Disease, Population)",
            recordsBefore,
            recordsAfterCleaning,
            missingHandled,
            duplicatesRemoved,
            invalidFiltered,
            10,
            "SUCCESS"
        );
    }

    public String determineRiskLabel(double ph, double turbidity, double doVal, double rainfall, int cases, int density, double sanitation) {
        double score = 0.0;

        if (turbidity > 5.0) score += 1.5;
        if (turbidity > 8.0) score += 1.5;
        if (ph < 6.5 || ph > 8.5) score += 1.0;
        if (doVal < 5.0) score += 1.5;

        if (rainfall > 60.0) score += 1.5;
        if (sanitation < 80.0) score += 1.5;
        if (sanitation < 75.0) score += 1.0;
        if (density > 1000) score += 1.0;
        if (density > 10000) score += 1.5;

        if (cases >= 25) score += 2.0;
        else if (cases >= 12) score += 1.0;

        if (score >= 6.0 || cases >= 25) {
            return "HIGH";
        } else if (score >= 3.5 || cases >= 12) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private void writeArffFile(String filePath, List<String[]> rows) {
        try {
            File file = FilePathResolver.resolve(filePath);
            if (file == null || !file.exists()) {
                file = new File(filePath);
            }
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("@relation aquasafe_disease_risk\n\n");
                fw.write("@attribute district {Chennai,Madurai,Coimbatore,Tiruchirappalli,Salem,Tirunelveli,Thanjavur,Vellore,Erode,Kanyakumari}\n");
                fw.write("@attribute ph numeric\n");
                fw.write("@attribute turbidity numeric\n");
                fw.write("@attribute tds numeric\n");
                fw.write("@attribute dissolved_oxygen numeric\n");
                fw.write("@attribute temperature numeric\n");
                fw.write("@attribute rainfall numeric\n");
                fw.write("@attribute humidity numeric\n");
                fw.write("@attribute previous_disease_cases numeric\n");
                fw.write("@attribute population_density numeric\n");
                fw.write("@attribute sanitation_coverage numeric\n");
                fw.write("@attribute risk_level {LOW,MEDIUM,HIGH}\n\n");
                fw.write("@data\n");
                for (String[] r : rows) {
                    fw.write(String.join(",", r) + "\n");
                }
            }
        } catch (Exception e) {
            log.error("Error writing training ARFF file: {}", e.getMessage());
        }
    }
}
