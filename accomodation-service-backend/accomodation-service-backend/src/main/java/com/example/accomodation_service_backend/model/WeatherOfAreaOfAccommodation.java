package com.example.accomodation_service_backend.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "dim_weatherofareaofaccommodation") // if your table was created lowercase, use "dim_weatherofareaofaccommodation"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherOfAreaOfAccommodation {

    @Id
    @Column(name = "WeatherOfAreaOfAccommodationSK", columnDefinition = "char(15)", length = 15, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String weatherOfAreaOfAccommodationSk;

    @Column(name = "WeatherOfAreaOfAccommodationCode", length = 30, nullable = false)
    private String weatherOfAreaOfAccommodationCode;

    @Column(name = "MonthSK", columnDefinition = "char(7)", length = 7, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String monthSk;

    @Column(name = "AverageTemperature", precision = 5, scale = 2)
    private BigDecimal averageTemperature;

    @Column(name = "AveragePrecipitation", precision = 6, scale = 2)
    private BigDecimal averagePrecipitation;

    @Column(name = "AccommodationLocationSK", columnDefinition = "char(12)", length = 12, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String accommodationLocationSk;
}

