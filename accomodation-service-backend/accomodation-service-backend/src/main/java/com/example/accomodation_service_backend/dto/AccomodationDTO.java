package com.example.accomodation_service_backend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccomodationDTO {


    @Column(name = "AccommodationName")
    private String accommodationName;


    @Column(name = "AccommodationAddress")
    private String accommodationAddress;

}
