package com.example.accomodation_service_backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastingDTO {

    @Column(name = "id")
    private String id;

    @Column(name = "AccommodationName")
    private String accommodationName;


    @Column(name = "AccommodationAddress")
    private String accommodationAddress;

    @Column(name = "Environment")
    private String environment;

    Map<Integer, Integer> forecastBookings = new LinkedHashMap<>();
}
