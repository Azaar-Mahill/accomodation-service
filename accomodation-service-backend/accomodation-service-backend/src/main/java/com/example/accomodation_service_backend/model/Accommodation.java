package com.example.accomodation_service_backend.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "dim_accommodation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {

    @Id
    @Column(name = "AccommodationSK", columnDefinition = "char(12)", length = 12, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR) // ensure CHAR(12), not VARCHAR
    private String accommodationSk;

    @Column(name = "AccommodationCode", length = 30, nullable = false, unique = true)
    private String accommodationCode;

    @Column(name = "AccommodationName", length = 150, nullable = false)
    private String accommodationName;

    @Column(name = "AccommodationTypeSK", columnDefinition = "char(14)", length = 14, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR) // matches CHAR(14) in DW
    private String accommodationTypeSk;

    @Column(name = "AccommodationLocationSK", columnDefinition = "char(12)", length = 12, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR) // matches CHAR(12) in DW
    private String accommodationLocationSk;
}
