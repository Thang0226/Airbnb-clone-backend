package com.codegym.model;

import com.codegym.model.constants.HouseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "houses")
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String houseName;
    @Column(length = 500)
    private String address;
    private int bedrooms;
    private int bathrooms;
    @Column(length = 2000)
    private String description;
    private int price;

    @Enumerated(EnumType.STRING)
    private HouseStatus status;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer rentals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    @JsonIgnore
    private User host;
}
