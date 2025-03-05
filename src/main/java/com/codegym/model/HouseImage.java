package com.codegym.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "house_images")
public class HouseImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "house_id")
    @JsonBackReference
    @ToString.Exclude
    private House house;
}
