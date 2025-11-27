package com.example.accomodation_service_backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccomodationTypeDTO {

    @Column(name = "AccommodationName")
    private String accommodationName;

    @Column(name = "Environment")
    private String environment;

    @Column(name = "AccommodationAddress")
    private String accommodationAddress;

    private Map<Integer, BigDecimal> avgTempByMonthC;

    private Map<Integer, BigDecimal> avgPrecipByMonthMm;
}
