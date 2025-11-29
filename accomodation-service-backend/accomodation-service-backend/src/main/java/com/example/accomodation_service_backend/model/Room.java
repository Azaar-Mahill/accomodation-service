package com.example.accomodation_service_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
@Entity
@Table(name = "dim_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @Column(name = "RoomSK", columnDefinition = "char(12)", nullable = false)
    private String roomSk;

    @Column(name = "RoomCode", length = 30, nullable = false)
    private String roomCode;

    @Column(name = "RoomName", length = 100, nullable = false)
    private String roomName;

    @Column(name = "NumberOfBeds", nullable = false)
    private Byte numberOfBeds;

    @Column(name = "ClassOfRoom", length = 40, nullable = false)
    private String classOfRoom;

    @Column(name = "RateOfRoom", precision = 10, scale = 2, nullable = false)
    private BigDecimal rateOfRoom;

    @Column(name = "AccommodationSK", columnDefinition = "char(12)", nullable = false)
    private String accommodationSk;   // or @ManyToOne if you want
}
