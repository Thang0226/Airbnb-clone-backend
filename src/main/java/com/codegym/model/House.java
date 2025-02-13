package com.codegym.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "houses")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    @Column(name = "houses_id")
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
    private List<HouseImage> images;

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
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Địa chỉ là bắt buộc")
    private String address;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Thành phố là bắt buộc")
    private String city;

    @Column(nullable = false)
    @NotNull(message = "Số lượng phòng ngủ là bắt buộc")
    @Min(value = 1, message = "Số lượng phòng ngủ phải từ 1 đến 10")
    @Max(value = 10, message = "Số lượng phòng ngủ phải từ 1 đến 10")
    private Integer numberOfBedrooms;

    @Column(nullable = false)
    @NotNull(message = "Số lượng phòng tắm là bắt buộc")
    @Min(value = 1, message = "Số lượng phòng tắm phải từ 1 đến 3")
    @Max(value = 3, message = "Số lượng phòng tắm phải từ 1 đến 3")
    private Integer numberOfBathrooms;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Giá tiền theo ngày là bắt buộc")
    private BigDecimal pricePerDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Trạng thái là bắt buộc")
    private HouseStatus status;

    // Ảnh: nếu không đăng thì có ảnh mặc định
    @Column(columnDefinition = "TEXT")
    private String imageFile = "default.png";

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public House() {
    }

    public House(Long id, UserTemporary host, List<HouseImage> images, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, String name, String address, String city, Integer numberOfBedrooms, Integer numberOfBathrooms, String description, BigDecimal pricePerDay, HouseStatus status, String imageFile, BigDecimal latitude, BigDecimal longitude, LocalDateTime updatedAt) {
        this.id = id;
        this.host = host;
        this.images = images;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.name = name;
        this.address = address;
        this.city = city;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.description = description;
        this.pricePerDay = pricePerDay;
        this.status = status;
        if (imageFile == null || imageFile.trim().isEmpty()) {
            this.imageFile = "default.png";
        } else {
            this.imageFile = imageFile;
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = updatedAt;
    }

    public House(String name, String address, Integer numberOfBedrooms, Integer numberOfBathrooms,
                 String description, BigDecimal pricePerDay, HouseStatus status, String imageFile) {
        this.name = name;
        this.address = address;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.description = description;
        this.pricePerDay = pricePerDay;
        this.status = status;
        // Nếu image rỗng hoặc null thì dùng ảnh mặc định
        if (imageFile == null || imageFile.trim().isEmpty()) {
            this.imageFile = "default.png";
        } else {
            this.imageFile = imageFile;
        }
    }

}
