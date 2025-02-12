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

    // Liên kết với House (house_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "houses_id", nullable = false)
    private HouseList house;

    @Column(name = "image_url", nullable = false, length = 255)
    @NotBlank(message = "URL ảnh là bắt buộc")
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;



}
