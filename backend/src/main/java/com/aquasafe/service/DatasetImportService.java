package com.aquasafe.service;

import com.aquasafe.dto.DatasetUploadResponseDTO;
import com.aquasafe.entity.*;
import com.aquasafe.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class DatasetImportService {

    private static final Logger log = LoggerFactory.getLogger(DatasetImportService.class);

    private final WaterQualityRepository waterQualityRepository;
    private final WeatherDataRepository weatherDataRepository;
    private final DiseaseRecordRepository diseaseRecordRepository;
    private final PopulationDataRepository populationDataRepository;
    private final DatasetImportRepository importRepository;

    public DatasetImportService(
            WaterQualityRepository waterQualityRepository,
            WeatherDataRepository weatherDataRepository,
            DiseaseRecordRepository diseaseRecordRepository,
            PopulationDataRepository populationDataRepository,
            DatasetImportRepository importRepository) {
        this.waterQualityRepository = waterQualityRepository;
        this.weatherDataRepository = weatherDataRepository;
        this.diseaseRecordRepository = diseaseRecordRepository;
        this.populationDataRepository = populationDataRepository;
        this.importRepository = importRepository;
    }

    @Transactional
    public DatasetUploadResponseDTO processCsvUpload(MultipartFile file, String datasetType) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The uploaded file is empty.");
        }
        return processCsvStream(file.getInputStream(), file.getOriginalFilename(), datasetType);
    }

    @Transactional
    public DatasetUploadResponseDTO processCsvFile(File file, String datasetType) throws Exception {
        if (!file.exists() || file.length() == 0) {
            throw new IllegalArgumentException("File not found or empty: " + file.getPath());
        }
        try (InputStream is = new FileInputStream(file)) {
            return processCsvStream(is, file.getName(), datasetType);
        }
    }

    @Transactional
    public DatasetUploadResponseDTO processCsvStream(InputStream inputStream, String filename, String datasetType) throws Exception {
        log.info("Processing upload for file: {}, datasetType: {}", filename, datasetType);

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            List<String[]> allRows = csvReader.readAll();
            if (allRows.isEmpty()) {
                throw new IllegalArgumentException("CSV file contains no data.");
            }

            String[] header = allRows.get(0);
            List<String[]> dataRows = allRows.subList(1, allRows.size());

            int totalRecords = dataRows.size();
            int validRecords = 0;
            int invalidRecords = 0;
            int duplicateRecords = 0;
            int missingValues = 0;

            List<Map<String, String>> previewRows = new ArrayList<>();
            Set<String> seenKeys = new HashSet<>();

            if ("water_quality".equalsIgnoreCase(datasetType)) {
                List<WaterQuality> entities = new ArrayList<>();
                for (String[] row : dataRows) {
                    if (row.length < 8) {
                        invalidRecords++;
                        continue;
                    }
                    if (hasMissingValues(row)) missingValues++;

                    try {
                        String district = row[0].trim();
                        String state = row[1].trim();
                        LocalDate date = LocalDate.parse(row[2].trim());
                        Double ph = Double.parseDouble(row[3].trim());
                        Double turbidity = Double.parseDouble(row[4].trim());
                        Double tds = Double.parseDouble(row[5].trim());
                        Double dissolvedOxygen = Double.parseDouble(row[6].trim());
                        Double waterTemperature = Double.parseDouble(row[7].trim());

                        String key = district.toLowerCase() + "_" + date;
                        if (seenKeys.contains(key)) {
                            duplicateRecords++;
                            continue;
                        }
                        seenKeys.add(key);

                        entities.add(new WaterQuality(district, state, date, ph, turbidity, tds, dissolvedOxygen, waterTemperature));
                        validRecords++;

                        if (previewRows.size() < 10) {
                            Map<String, String> p = new LinkedHashMap<>();
                            p.put("district", district);
                            p.put("date", date.toString());
                            p.put("ph", ph.toString());
                            p.put("turbidity", turbidity.toString());
                            p.put("tds", tds.toString());
                            p.put("dissolvedOxygen", dissolvedOxygen.toString());
                            previewRows.add(p);
                        }
                    } catch (Exception ex) {
                        invalidRecords++;
                    }
                }
                waterQualityRepository.saveAll(entities);

            } else if ("weather_data".equalsIgnoreCase(datasetType)) {
                List<WeatherData> entities = new ArrayList<>();
                for (String[] row : dataRows) {
                    if (row.length < 6) {
                        invalidRecords++;
                        continue;
                    }
                    if (hasMissingValues(row)) missingValues++;

                    try {
                        String district = row[0].trim();
                        String state = row[1].trim();
                        LocalDate date = LocalDate.parse(row[2].trim());
                        Double temp = Double.parseDouble(row[3].trim());
                        Double rain = Double.parseDouble(row[4].trim());
                        Double humidity = Double.parseDouble(row[5].trim());

                        String key = district.toLowerCase() + "_" + date;
                        if (seenKeys.contains(key)) {
                            duplicateRecords++;
                            continue;
                        }
                        seenKeys.add(key);

                        entities.add(new WeatherData(district, state, date, temp, rain, humidity, "STORED DATA"));
                        validRecords++;

                        if (previewRows.size() < 10) {
                            Map<String, String> p = new LinkedHashMap<>();
                            p.put("district", district);
                            p.put("date", date.toString());
                            p.put("temperature", temp.toString());
                            p.put("rainfall", rain.toString());
                            p.put("humidity", humidity.toString());
                            previewRows.add(p);
                        }
                    } catch (Exception ex) {
                        invalidRecords++;
                    }
                }
                weatherDataRepository.saveAll(entities);

            } else if ("disease_records".equalsIgnoreCase(datasetType)) {
                List<DiseaseRecord> entities = new ArrayList<>();
                for (String[] row : dataRows) {
                    if (row.length < 7) {
                        invalidRecords++;
                        continue;
                    }
                    if (hasMissingValues(row)) missingValues++;

                    try {
                        String district = row[0].trim();
                        String state = row[1].trim();
                        LocalDate date = LocalDate.parse(row[2].trim());
                        Integer cholera = Integer.parseInt(row[3].trim());
                        Integer typhoid = Integer.parseInt(row[4].trim());
                        Integer diarrhea = Integer.parseInt(row[5].trim());
                        Integer hepatitis = Integer.parseInt(row[6].trim());

                        String key = district.toLowerCase() + "_" + date;
                        if (seenKeys.contains(key)) {
                            duplicateRecords++;
                            continue;
                        }
                        seenKeys.add(key);

                        entities.add(new DiseaseRecord(district, state, date, cholera, typhoid, diarrhea, hepatitis));
                        validRecords++;

                        if (previewRows.size() < 10) {
                            Map<String, String> p = new LinkedHashMap<>();
                            p.put("district", district);
                            p.put("date", date.toString());
                            p.put("cholera", cholera.toString());
                            p.put("typhoid", typhoid.toString());
                            p.put("diarrhea", diarrhea.toString());
                            p.put("hepatitis", hepatitis.toString());
                            previewRows.add(p);
                        }
                    } catch (Exception ex) {
                        invalidRecords++;
                    }
                }
                diseaseRecordRepository.saveAll(entities);

            } else if ("population_data".equalsIgnoreCase(datasetType)) {
                List<PopulationData> entities = new ArrayList<>();
                for (String[] row : dataRows) {
                    if (row.length < 7) {
                        invalidRecords++;
                        continue;
                    }
                    if (hasMissingValues(row)) missingValues++;

                    try {
                        String district = row[0].trim();
                        String state = row[1].trim();
                        Long pop = Long.parseLong(row[2].trim());
                        Integer density = Integer.parseInt(row[3].trim());
                        Double sanitation = Double.parseDouble(row[4].trim());
                        Double lat = Double.parseDouble(row[5].trim());
                        Double lon = Double.parseDouble(row[6].trim());

                        if (populationDataRepository.existsByDistrictIgnoreCase(district)) {
                            duplicateRecords++;
                            continue;
                        }

                        entities.add(new PopulationData(district, state, pop, density, sanitation, lat, lon));
                        validRecords++;

                        if (previewRows.size() < 10) {
                            Map<String, String> p = new LinkedHashMap<>();
                            p.put("district", district);
                            p.put("population", pop.toString());
                            p.put("density", density.toString());
                            p.put("sanitation", sanitation.toString());
                            p.put("lat", lat.toString());
                            p.put("lon", lon.toString());
                            previewRows.add(p);
                        }
                    } catch (Exception ex) {
                        invalidRecords++;
                    }
                }
                populationDataRepository.saveAll(entities);
            } else {
                throw new IllegalArgumentException("Unknown dataset type: " + datasetType + ". Supported: water_quality, weather_data, disease_records, population_data");
            }

            // Save import history log
            DatasetImport importLog = new DatasetImport(
                filename, datasetType, totalRecords, validRecords, invalidRecords, duplicateRecords, missingValues, "COMPLETED"
            );
            importRepository.save(importLog);

            DatasetUploadResponseDTO response = new DatasetUploadResponseDTO();
            response.setFilename(filename);
            response.setDatasetType(datasetType);
            response.setTotalRecords(totalRecords);
            response.setValidRecords(validRecords);
            response.setInvalidRecords(invalidRecords);
            response.setDuplicateRecords(duplicateRecords);
            response.setMissingValues(missingValues);
            response.setStatus("COMPLETED");
            response.setPreviewRows(previewRows);

            return response;
        }
    }

    private boolean hasMissingValues(String[] row) {
        for (String cell : row) {
            if (cell == null || cell.trim().isEmpty() || cell.trim().equalsIgnoreCase("null") || cell.trim().equalsIgnoreCase("na")) {
                return true;
            }
        }
        return false;
    }

    public List<DatasetImport> getRecentImports() {
        return importRepository.findTop10ByOrderByImportTimeDesc();
    }

    public Page<DatasetImport> getAllImports(int page, int size) {
        return importRepository.findAllByOrderByImportTimeDesc(PageRequest.of(page, size));
    }
}
