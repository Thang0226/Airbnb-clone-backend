package com.codegym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.Data;

import java.time.LocalDateTime;

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
