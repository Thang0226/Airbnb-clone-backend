package com.codegym.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "houses_images")
public class HouseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long image_id;

    // Không cần ánh xạ ManyToOne vì quan hệ sẽ được định nghĩa qua join table ở HouseList
    // private HouseList house;

    @Column(name = "image_url", nullable = false, length = 255)
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public HouseImage() {}

    public HouseImage(String imageUrl, LocalDateTime createdAt) {
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public @NotBlank(message = "Image URL is required") String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NotBlank(message = "Image URL is required") String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


// Getters and setters
}
