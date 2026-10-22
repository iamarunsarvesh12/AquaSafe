package com.aquasafe.spark;

import com.aquasafe.entity.DiseaseRecord;
import com.aquasafe.entity.WaterQuality;
import com.aquasafe.entity.WeatherData;

import java.util.List;

public interface DataProcessingService {

    DataProcessingResult processAggregates(List<WaterQuality> wqList, List<WeatherData> weatherList, List<DiseaseRecord> diseaseList);

    String getEngineName();

    boolean isAvailable();
}
