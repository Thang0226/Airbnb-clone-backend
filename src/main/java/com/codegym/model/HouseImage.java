package com.codegym.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "houses_images")
public class HouseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Getters and setters
    public Long getImage_id() {
        return image_id;
    }

    public void setImage_id(Long image_id) {
        this.image_id = image_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
