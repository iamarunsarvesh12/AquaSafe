package com.aquasafe.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "disease_records", indexes = {
    @Index(name = "idx_disease_district", columnList = "district"),
    @Index(name = "idx_disease_date", columnList = "date"),
    @Index(name = "idx_disease_district_date", columnList = "district, date")
})
public class DiseaseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_code", length = 20)
    private String districtCode;

    @Column(name = "district_name", length = 100)
    private String districtName;

    @Column(length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String state = "Tamil Nadu";

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "reporting_period", length = 30)
    private String reportingPeriod = "MONTHLY";

    private Long population;

    @Column(name = "cholera_cases", nullable = false)
    private Integer choleraCases = 0;

    @Column(name = "typhoid_cases", nullable = false)
    private Integer typhoidCases = 0;

    @Column(name = "diarrhea_cases", nullable = false)
    private Integer diarrheaCases = 0;

    @Column(name = "hepatitis_cases", nullable = false)
    private Integer hepatitisCases = 0;

    @Column(name = "total_cases", nullable = false)
    private Integer totalCases = 0;

    @Column(name = "incidence_rate_per_100k")
    private Double incidenceRatePer100k;

    @Column(name = "data_source", length = 100)
    private String dataSource = "IDSP_WEEKLY_SURVEILLANCE";

    @Column(name = "reporting_status", length = 50)
    private String reportingStatus = "CONFIRMED";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DiseaseRecord() {}

    public DiseaseRecord(String district, String state, LocalDate date, Integer choleraCases, Integer typhoidCases, Integer diarrheaCases, Integer hepatitisCases) {
        this.district = district;
        this.districtName = district;
        this.state = state;
        this.date = date;
        this.choleraCases = choleraCases != null ? choleraCases : 0;
        this.typhoidCases = typhoidCases != null ? typhoidCases : 0;
        this.diarrheaCases = diarrheaCases != null ? diarrheaCases : 0;
        this.hepatitisCases = hepatitisCases != null ? hepatitisCases : 0;
        this.totalCases = this.choleraCases + this.typhoidCases + this.diarrheaCases + this.hepatitisCases;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getTotalCases() {
        if (totalCases != null && totalCases > 0) return totalCases;
        return (choleraCases != null ? choleraCases : 0) +
               (typhoidCases != null ? typhoidCases : 0) +
               (diarrheaCases != null ? diarrheaCases : 0) +
               (hepatitisCases != null ? hepatitisCases : 0);
    }

    public void setTotalCases(Integer totalCases) { this.totalCases = totalCases; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getDistrictId() { return districtId; }
    public void setDistrictId(Integer districtId) { this.districtId = districtId; }

    public String getDistrictCode() { return districtCode; }
    public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }

    public String getDistrictName() { return districtName != null ? districtName : district; }
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
        if (this.district == null) this.district = districtName;
    }

    public String getDistrict() { return district != null ? district : districtName; }
    public void setDistrict(String district) {
        this.district = district;
        if (this.districtName == null) this.districtName = district;
    }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getReportingPeriod() { return reportingPeriod; }
    public void setReportingPeriod(String reportingPeriod) { this.reportingPeriod = reportingPeriod; }

    public Long getPopulation() { return population; }
    public void setPopulation(Long population) { this.population = population; }

    public Integer getCholeraCases() { return choleraCases; }
    public void setCholeraCases(Integer choleraCases) { this.choleraCases = choleraCases; }

    public Integer getTyphoidCases() { return typhoidCases; }
    public void setTyphoidCases(Integer typhoidCases) { this.typhoidCases = typhoidCases; }

    public Integer getDiarrheaCases() { return diarrheaCases; }
    public void setDiarrheaCases(Integer diarrheaCases) { this.diarrheaCases = diarrheaCases; }

    public Integer getHepatitisCases() { return hepatitisCases; }
    public void setHepatitisCases(Integer hepatitisCases) { this.hepatitisCases = hepatitisCases; }

    public Double getIncidenceRatePer100k() { return incidenceRatePer100k; }
    public void setIncidenceRatePer100k(Double incidenceRatePer100k) { this.incidenceRatePer100k = incidenceRatePer100k; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public String getReportingStatus() { return reportingStatus; }
    public void setReportingStatus(String reportingStatus) { this.reportingStatus = reportingStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
