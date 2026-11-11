package com.aquasafe.service;

import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.PopulationData;
import com.aquasafe.repository.DiseaseRecordRepository;
import com.aquasafe.repository.PopulationDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiseaseService {

    private final DiseaseRecordRepository diseaseRepository;
    private final PopulationDataRepository populationRepository;

    public DiseaseService(DiseaseRecordRepository diseaseRepository, PopulationDataRepository populationRepository) {
        this.diseaseRepository = diseaseRepository;
        this.populationRepository = populationRepository;
    }

    public Page<DiseaseRecord> getRecords(String district, int page, int size) {
        if (district != null && !district.trim().isEmpty()) {
            return diseaseRepository.findByDistrictContainingIgnoreCase(district, PageRequest.of(page, size));
        }
        return diseaseRepository.findAll(PageRequest.of(page, size));
    }

    public List<DiseaseRecord> getRecordsByDistrict(String district) {
        return diseaseRepository.findByDistrictOrderByDateDesc(district);
    }

    public List<DiseaseRecord> getRecordsByDateRange(LocalDate start, LocalDate end) {
        return diseaseRepository.findByDateBetweenOrderByDateAsc(start, end);
    }

    public List<Object[]> getTotalDiseaseCases() {
        return diseaseRepository.getTotalDiseaseCases();
    }

    public List<Object[]> getDistrictWiseTotals() {
        return diseaseRepository.getDistrictWiseTotals();
    }

    public List<Object[]> getTimelineTotals() {
        return diseaseRepository.getTimelineTotals();
    }

    public Map<String, Object> getDiseaseAnalytics() {
        List<DiseaseRecord> all = diseaseRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        if (all.isEmpty()) {
            result.put("totalRecords", 0);
            return result;
        }

        long totalCholera = all.stream().mapToLong(DiseaseRecord::getCholeraCases).sum();
        long totalTyphoid = all.stream().mapToLong(DiseaseRecord::getTyphoidCases).sum();
        long totalDiarrhea = all.stream().mapToLong(DiseaseRecord::getDiarrheaCases).sum();
        long totalHepatitis = all.stream().mapToLong(DiseaseRecord::getHepatitisCases).sum();
        long grandTotal = totalCholera + totalTyphoid + totalDiarrhea + totalHepatitis;

        result.put("totalObservations", all.size());
        result.put("grandTotalCases", grandTotal);
        result.put("choleraCases", totalCholera);
        result.put("typhoidCases", totalTyphoid);
        result.put("diarrheaCases", totalDiarrhea);
        result.put("hepatitisCases", totalHepatitis);

        // Proportions
        if (grandTotal > 0) {
            result.put("choleraPercentage", Math.round((totalCholera * 1000.0) / grandTotal) / 10.0);
            result.put("typhoidPercentage", Math.round((totalTyphoid * 1000.0) / grandTotal) / 10.0);
            result.put("diarrheaPercentage", Math.round((totalDiarrhea * 1000.0) / grandTotal) / 10.0);
            result.put("hepatitisPercentage", Math.round((totalHepatitis * 1000.0) / grandTotal) / 10.0);
        }

        // Monthly trends
        Map<String, Long> monthlyTotals = all.stream().collect(Collectors.groupingBy(
            d -> d.getDate().getYear() + "-" + String.format("%02d", d.getDate().getMonthValue()),
            TreeMap::new,
            Collectors.summingLong(DiseaseRecord::getTotalCases)
        ));
        result.put("monthlyTrends", monthlyTotals);

        return result;
    }

    public List<Map<String, Object>> getDistrictIncidenceRates() {
        List<DiseaseRecord> all = diseaseRepository.findAll();
        List<PopulationData> populations = populationRepository.findAll();

        Map<String, Long> popMap = populations.stream()
                .collect(Collectors.toMap(p -> p.getDistrict().toLowerCase(), PopulationData::getPopulation, (a, b) -> a));

        Map<String, List<DiseaseRecord>> byDist = all.stream()
                .collect(Collectors.groupingBy(d -> d.getDistrict().toLowerCase()));

        List<Map<String, Object>> rates = new ArrayList<>();

        for (Map.Entry<String, List<DiseaseRecord>> entry : byDist.entrySet()) {
            String distLower = entry.getKey();
            List<DiseaseRecord> distRecords = entry.getValue();

            long totalCases = distRecords.stream().mapToLong(DiseaseRecord::getTotalCases).sum();
            long pop = popMap.getOrDefault(distLower, 2000000L);

            // Incidence Rate = (cases / pop) * 100,000
            double incidenceRate = Math.round(((double) totalCases / pop) * 100000.0 * 100.0) / 100.0;

            String distName = distRecords.get(0).getDistrict();

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("district", distName);
            m.put("population", pop);
            m.put("totalCases", totalCases);
            m.put("incidenceRatePer100k", incidenceRate);
            m.put("riskClassification", incidenceRate >= 15.0 ? "HIGH" : (incidenceRate >= 6.0 ? "MEDIUM" : "LOW"));
            rates.add(m);
        }

        rates.sort((a, b) -> Double.compare((Double) b.get("incidenceRatePer100k"), (Double) a.get("incidenceRatePer100k")));
        return rates;
    }
}
