package com.example.accomodation_service_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accommodations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {

    @Id
    @Column(length = 16)
    private String id;           // keep string ids like "ACC001"

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String environment;  // Beach, Hill Country, City, Wildlife, Cultural

    @Column(nullable = false)
    private String type;         // Hotel, Resort, Villa, Guest House, Hostel
}

