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
public class AccomodationKPIDTO {

    @Column(name = "id")
    private String id;

    @Column(name = "AccommodationName")
    private String accommodationName;


    @Column(name = "AccommodationAddress")
    private String accommodationAddress;

    @Column(name = "Environment")
    private String environment;

    private Map<Integer, Integer> bookingsByMonth;

    private Map<Integer, BigDecimal> revenueByMonth;

    private Map<Integer, BigDecimal> averageDailyRate;
}
