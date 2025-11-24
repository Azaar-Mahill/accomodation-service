package com.example.accomodation_service_backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccomodationWeatherDTO {

    @Column(name = "AccommodationName")
    private String accommodationName;


    @Column(name = "AccommodationAddress")
    private String accommodationAddress;

    @Column(name = "Temperature")
    private String temperature;

    @Column(name = "Precipitation")
    private String precipitation;

    @Column(name = "weatherStatus")
    private String weatherStatus;

    @Column(name = "Environment")
    private String environment;
}
