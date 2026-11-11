package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_quality_records", indexes = {
    @Index(name = "idx_wqr_dist", columnList = "districtName"),
    @Index(name = "idx_wqr_date", columnList = "observationDate"),
    @Index(name = "idx_wqr_dist_date", columnList = "districtName, observationDate")
})
public class WaterQualityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_code", length = 20)
    private String districtCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "station_name", length = 150)
    private String stationName;

    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(name = "observation_type", length = 50)
    private String observationType = "GROUNDWATER_LAB"; // GROUNDWATER_LAB, SURFACE_WATER_LAB, SENSOR

    @Column(nullable = false)
    private Double ph;

    private Double turbidity;

    @Column(nullable = false)
    private Double tds;

    @Column(name = "dissolved_oxygen")
    private Double dissolvedOxygen;

    @Column(name = "electrical_conductivity")
    private Double electricalConductivity;

    private Double chloride;
    private Double nitrate;
    private Double fluoride;

    @Column(name = "water_temperature")
    private Double waterTemperature;

    @Column(name = "turbidity_available")
    private Boolean turbidityAvailable = true;

    @Column(name = "do_available")
    private Boolean doAvailable = true;

    @Column(name = "data_source", length = 100)
    private String dataSource = "CENTRAL_GROUND_WATER_BOARD_TN";

    @Column(name = "validation_status", length = 50)
    private String validationStatus = "VALIDATED";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public WaterQualityRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDistrictId() { return districtId; }
    public void setDistrictId(Integer districtId) { this.districtId = districtId; }

    public String getDistrictCode() { return districtCode; }
    public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public LocalDate getObservationDate() { return observationDate; }
    public void setObservationDate(LocalDate observationDate) { this.observationDate = observationDate; }

    public String getObservationType() { return observationType; }
    public void setObservationType(String observationType) { this.observationType = observationType; }

    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }

    public Double getTurbidity() { return turbidity; }
    public void setTurbidity(Double turbidity) { this.turbidity = turbidity; }

    public Double getTds() { return tds; }
    public void setTds(Double tds) { this.tds = tds; }

    public Double getDissolvedOxygen() { return dissolvedOxygen; }
    public void setDissolvedOxygen(Double dissolvedOxygen) { this.dissolvedOxygen = dissolvedOxygen; }

    public Double getElectricalConductivity() { return electricalConductivity; }
    public void setElectricalConductivity(Double electricalConductivity) { this.electricalConductivity = electricalConductivity; }

    public Double getChloride() { return chloride; }
    public void setChloride(Double chloride) { this.chloride = chloride; }

    public Double getNitrate() { return nitrate; }
    public void setNitrate(Double nitrate) { this.nitrate = nitrate; }

    public Double getFluoride() { return fluoride; }
    public void setFluoride(Double fluoride) { this.fluoride = fluoride; }

    public Double getWaterTemperature() { return waterTemperature; }
    public void setWaterTemperature(Double waterTemperature) { this.waterTemperature = waterTemperature; }

    public Boolean getTurbidityAvailable() { return turbidityAvailable; }
    public void setTurbidityAvailable(Boolean turbidityAvailable) { this.turbidityAvailable = turbidityAvailable; }

    public Boolean getDoAvailable() { return doAvailable; }
    public void setDoAvailable(Boolean doAvailable) { this.doAvailable = doAvailable; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

