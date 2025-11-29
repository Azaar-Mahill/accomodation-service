package com.example.accomodation_service_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Entity
@Table(name = "fact_bookaccommodation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAccomodation {

    @Id
    @Column(name = "BookAccommodationSK", columnDefinition = "char(12)", nullable = false)
    private String bookAccommodationSk;

    @Column(name = "BookAccommodationCode", length = 30, nullable = false)
    private String bookAccommodationCode;

    @Column(name = "CustomerSK", columnDefinition = "char(12)", nullable = false)
    private String customerSk;

    @Column(name = "PaymentSK", columnDefinition = "char(12)", nullable = false)
    private String paymentSk;

    @Column(name = "RoomSK", columnDefinition = "char(12)", nullable = false)
    private String roomSk;

    @Column(name = "CheckinDateSK", columnDefinition = "char(10)", nullable = false)
    private String checkinDateSk;

    @Column(name = "CheckoutDateSK", columnDefinition = "char(10)", nullable = false)
    private String checkoutDateSk;

    @Column(name = "Discount", precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(name = "TotalAmount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;
}
