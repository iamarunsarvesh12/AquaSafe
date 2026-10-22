package com.aquasafe.spark;

import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.WaterQuality;
import com.aquasafe.entity.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataProcessingDispatcher {

    private static final Logger log = LoggerFactory.getLogger(DataProcessingDispatcher.class);

    private final DataProcessingService localService;
    private final SparkDataProcessingService sparkService;

    public DataProcessingDispatcher(
            @Qualifier("localDataProcessingService") DataProcessingService localService,
            SparkDataProcessingService sparkService) {
        this.localService = localService;
        this.sparkService = sparkService;
    }

    public DataProcessingResult executeProcessing(List<WaterQuality> wqList, List<WeatherData> weatherList, List<DiseaseRecord> diseaseList) {
        if (sparkService.isAvailable()) {
            log.info("Spark cluster is active. Delegating workload to SparkDataProcessingService...");
            return sparkService.processAggregates(wqList, weatherList, diseaseList);
        } else {
            log.info("Spark cluster is not reachable or unconfigured. Falling back automatically to LocalDataProcessingService...");
            return localService.processAggregates(wqList, weatherList, diseaseList);
        }
    }

    public SparkDataProcessingService getSparkService() {
        return sparkService;
    }
}
