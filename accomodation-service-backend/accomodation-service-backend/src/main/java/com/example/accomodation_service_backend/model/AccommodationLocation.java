package com.example.accomodation_service_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "dim_accommodationlocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationLocation {

    @Id
    @Column(name = "AccommodationLocationSK", columnDefinition = "char(12)", length = 12, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR) // Hibernate 6: ensures CHAR mapping instead of VARCHAR
    private String accommodationLocationSk;

    @Column(name = "AccommodationLocationCode", length = 30, nullable = false, unique = true)
    private String accommodationLocationCode;

    @Column(name = "Province", length = 50, nullable = false)
    private String province;

    @Column(name = "District", length = 50, nullable = false)
    private String district;

    @Column(name = "City", length = 80, nullable = false)
    private String city;

    @Column(name = "Environment", length = 40, nullable = false)
    private String environment; // Beach, Hill Country, City, Wildlife, etc.
}

