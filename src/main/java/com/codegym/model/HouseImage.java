package com.codegym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "house_images")
public class HouseImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    private String fileName;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "house_id")
    @JsonBackReference
    @JsonIgnoreProperties("houseImages")
    private House house;

    // Constructors
    public HouseImage() {}

    public HouseImage(String imageUrl, LocalDateTime createdAt) {
        this.fileName = imageUrl;
        this.createdAt = createdAt;
    }

    public @NotBlank(message = "Image URL is required") String getImageUrl() {
        return fileName;
    }

    public void setImageUrl(@NotBlank(message = "Image URL is required") String imageUrl) {
        this.fileName = imageUrl;
    }
}
