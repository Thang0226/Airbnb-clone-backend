package com.codegym.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String description;
    private int price;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<HouseImage> houseImages = new ArrayList<>();

  // Liên kết với user được phân quyền là chủ nhà (host) , một user có thể sở hữu nhiều ngôi nhà
    @ManyToOne(fetch = FetchType.LAZY)
    //fetch = FetchType.LAZY nghĩa là khi lấy dữ liệu của House từ cơ sở dữ liệu, thông tin của User liên quan sẽ không được tải ngay lập tức (lazy loading). Thông tin User chỉ được tải khi bạn thực sự cần truy cập đến nó, giúp giảm tải
    @JoinColumn(name = "host_id", nullable = false)
    @JsonIgnore
    private User host;

}
