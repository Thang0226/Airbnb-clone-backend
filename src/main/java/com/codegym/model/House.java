package com.codegym.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "houses")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    // Liên kết với user được phân quyền là chủ nhà (host) , một user có thể sở hữu nhiều ngôi nhà
    @ManyToOne(fetch = FetchType.LAZY)
    //fetch = FetchType.LAZY nghĩa là khi lấy dữ liệu của House từ cơ sở dữ liệu, thông tin của User liên quan sẽ không được tải ngay lập tức (lazy loading). Thông tin User chỉ được tải khi bạn thực sự cần truy cập đến nó, giúp giảm tải
    @JoinColumn(name = "host_id", nullable = false)
    @JsonIgnore
    private UserTemporary host; // Giả sử entity User đã được định nghĩa

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "house_image_join",
            joinColumns = @JoinColumn(name = "house_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @JsonProperty("images")
    private List<HouseImage> houseImages;

    @Column(nullable = false)
    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    private LocalDate startDate;


    @Column(nullable = false)
    @NotNull(message = "Ngày kết thúc là bắt buộc")
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    //Not Blank chỉ dành cho kiểu dữ liệu String, đảm bảo rằng chuỗi không chỉ không null mà còn không rỗng (empty) hoặc chỉ chứa khoảng trắng.
    @NotBlank(message = "Tên của căn nhà là bắt buộc")
    private String houseName;

    @Column(nullable = false)
    @NotBlank(message = "Địa chỉ là bắt buộc")
    private String address;


    @Column(nullable = false)
    @NotNull(message = "Số lượng phòng ngủ là bắt buộc")
    @Min(value = 1, message = "Số lượng phòng ngủ phải từ 1 đến 10")
    @Max(value = 10, message = "Số lượng phòng ngủ phải từ 1 đến 10")
    private Integer bedrooms;

    @Column(nullable = false)
    @NotNull(message = "Số lượng phòng tắm là bắt buộc")
    @Min(value = 1, message = "Số lượng phòng tắm phải từ 1 đến 3")
    @Max(value = 3, message = "Số lượng phòng tắm phải từ 1 đến 3")
    private Integer bathrooms;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Giá tiền theo ngày là bắt buộc")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Trạng thái là bắt buộc")
    private HouseStatus status;



    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
