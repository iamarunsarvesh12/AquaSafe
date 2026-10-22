package com.aquasafe.config;

import com.aquasafe.entity.PredictionResult;
import com.aquasafe.entity.User;
import com.aquasafe.ml.WekaModelService;
import com.aquasafe.repository.*;
import com.aquasafe.service.DatasetImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PopulationDataRepository populationRepository;
    private final WaterQualityRepository waterQualityRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final DiseaseRecordRepository diseaseRecordRepository;
    private final PredictionResultRepository predictionRepository;
    private final DatasetImportService importService;
    private final WekaModelService mlService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            PopulationDataRepository populationRepository,
            WaterQualityRepository waterQualityRepository,
            WeatherDataRepository weatherDataRepository,
            DiseaseRecordRepository diseaseRecordRepository,
            PredictionResultRepository predictionRepository,
            DatasetImportService importService,
            WekaModelService mlService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.populationRepository = populationRepository;
        this.waterQualityRepository = waterQualityRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.diseaseRecordRepository = diseaseRecordRepository;
        this.predictionRepository = predictionRepository;
        this.importService = importService;
        this.mlService = mlService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking AquaSafe database initialization and seed datasets...");

        // 1. Seed Users
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "ADMIN");
            userRepository.save(admin);
            log.info("Created default administrator: admin / admin123");
        }
        if (!userRepository.existsByUsername("analyst")) {
            User user = new User("analyst", passwordEncoder.encode("user123"), "USER");
            userRepository.save(user);
            log.info("Created default public health user: analyst / user123");
        }

        // 2. Seed Population data if empty
        if (populationRepository.count() == 0) {
            File popFile = FilePathResolver.resolve("data/population_data.csv");
            if (popFile != null && popFile.exists()) {
                importService.processCsvFile(popFile, "population_data");
                log.info("Imported initial Tamil Nadu population and demographic dataset from {}", popFile.getAbsolutePath());
            }
        }

        // 3. Seed Water Quality data if empty
        if (waterQualityRepository.count() == 0) {
            File wqFile = FilePathResolver.resolve("data/water_quality.csv");
            if (wqFile != null && wqFile.exists()) {
                importService.processCsvFile(wqFile, "water_quality");
                log.info("Imported demonstration water quality time series from {}", wqFile.getAbsolutePath());
            }
        }

        // 4. Seed Weather data if empty
        if (weatherDataRepository.count() == 0) {
            File weatherFile = FilePathResolver.resolve("data/weather_data.csv");
            if (weatherFile != null && weatherFile.exists()) {
                importService.processCsvFile(weatherFile, "weather_data");
                log.info("Imported demonstration weather time series from {}", weatherFile.getAbsolutePath());
            }
        }

        // 5. Seed Disease data if empty
        if (diseaseRecordRepository.count() == 0) {
            File diseaseFile = FilePathResolver.resolve("data/disease_records.csv");
            if (diseaseFile != null && diseaseFile.exists()) {
                importService.processCsvFile(diseaseFile, "disease_records");
                log.info("Imported historical disease records from {}", diseaseFile.getAbsolutePath());
            }
        }

        // 6. Ensure ML model is trained
        if (!mlService.isModelLoaded()) {
            File arff = FilePathResolver.resolve("ml/training/training_dataset.arff");
            if (arff != null && arff.exists()) {
                log.info("Found training ARFF at {}. Training Random Forest...", arff.getAbsolutePath());
                mlService.trainModel();
            }
        }

        // 7. Seed initial prediction snapshots for map and KPIs if empty
        if (predictionRepository.count() == 0) {
            seedInitialPredictions();
        }

        log.info("AquaSafe application initialization sequence complete.");
    }

    private void seedInitialPredictions() {
        String[][] demo = {
            {"Chennai", "MEDIUM", "0.6840"},
            {"Madurai", "LOW", "0.8120"},
            {"Coimbatore", "LOW", "0.8950"},
            {"Tiruchirappalli", "MEDIUM", "0.6210"},
            {"Salem", "HIGH", "0.8420"},
            {"Tirunelveli", "LOW", "0.7780"},
            {"Thanjavur", "HIGH", "0.7930"},
            {"Vellore", "MEDIUM", "0.6510"},
            {"Erode", "LOW", "0.8350"},
            {"Kanyakumari", "LOW", "0.9120"}
        };
        for (String[] row : demo) {
            PredictionResult p = new PredictionResult(row[0], row[1], Double.parseDouble(row[2]), "RandomForest-v1.0");
            p.setPredictionDate(LocalDateTime.now().minusHours((long) (Math.random() * 24)));
            predictionRepository.save(p);
        }
    }
}
