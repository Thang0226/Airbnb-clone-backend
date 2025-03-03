package com.codegym.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate updatedAt;

    private Integer rating;

    private String comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="booking_id", nullable = false)
    private Booking booking;

    public Review() {
        this.updatedAt = LocalDate.now();
    }
}
