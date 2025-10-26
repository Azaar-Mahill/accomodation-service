package com.example.accomodation_service_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "dim_safetyofareaofaccommodation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafetyOfAreaOfAccommodation {

    @Id
    @Column(name = "SafetyOfAreaOfAccommodationSK", columnDefinition = "char(12)", length = 12, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String safetyOfAreaOfAccommodationSk;

    @Column(name = "SafetyOfAreaOfAccommodationCode", length = 30, nullable = false, unique = true)
    private String safetyOfAreaOfAccommodationCode;

    @Column(name = "MonthSK", columnDefinition = "char(7)", length = 7, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String monthSk;

    @Column(name = "AccidentRate", precision = 6, scale = 3)
    private BigDecimal accidentRate;

    @Column(name = "CrimeRate", precision = 6, scale = 3)
    private BigDecimal crimeRate;


    @Column(name = "AccommodationLocationSK", columnDefinition = "char(12)", length = 12, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String accommodationLocationSk;
}

