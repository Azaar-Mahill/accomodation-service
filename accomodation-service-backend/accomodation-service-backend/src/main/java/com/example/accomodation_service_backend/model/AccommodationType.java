package com.example.accomodation_service_backend.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "dim_accommodationtype")
@Data @NoArgsConstructor @AllArgsConstructor
public class AccommodationType {

    @Id
    @Column(name = "AccommodationTypeSK", columnDefinition = "char(14)", length = 14, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR) // Hibernate 6: map as CHAR, not VARCHAR
    private String accommodationTypeSk;

    @Column(name = "AccommodationTypeCode", length = 30, nullable = false, unique = true)
    private String accommodationTypeCode;

    @Column(name = "AccommodationTypeName", length = 80, nullable = false)
    private String accommodationTypeName;

    @Column(name = "NumberOfRooms")
    private Integer numberOfRooms;
}