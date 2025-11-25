package com.example.accomodation_service_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherOfAreaOfAccommodation2 {


    private String weatherOfAreaOfAccommodationSk;

    private String weatherOfAreaOfAccommodationCode;

    private String monthSk;

    private BigDecimal averageTemperature;

    private BigDecimal averagePrecipitation;

    private String accommodationLocationSk;

    private String weatherStatus;

    private BigDecimal crimeRate;

    private BigDecimal accidentRate;

    private String safetyStatus;
}
