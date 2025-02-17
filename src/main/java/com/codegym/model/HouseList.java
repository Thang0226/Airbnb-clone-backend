package com.codegym.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "houses")
public class HouseList {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long houses_id;


    // Liên kết với user được phân quyền là chủ nhà (host) , một user có thể sở hữu nhiều ngôi nhà
    @ManyToOne(fetch = FetchType.LAZY)   //fetch = FetchType.LAZY nghĩa là khi lấy dữ liệu của House từ cơ sở dữ liệu, thông tin của User liên quan sẽ không được tải ngay lập tức (lazy loading). Thông tin User chỉ được tải khi bạn thực sự cần truy cập đến nó, giúp giảm tải
    @JoinColumn(name = "host_id", nullable = false)
    private User_Temporary host_id; // Giả sử entity User đã được định nghĩa




    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "house_image_join",
            joinColumns = @JoinColumn(name = "house_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    @JsonProperty("image")
    private List<HouseImage> images;


    @Column( nullable = false)
    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    private LocalDate startDate;



    @Column(nullable = false)
    @NotNull(message = "Ngày kết thúc là bắt buộc")
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 255)
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
    @NotNull(message = "Status là bắt buộc")
    private HouseListStatusEnum status;

    // Ảnh: nếu không đăng thì có ảnh mặc định
    @Column(columnDefinition = "TEXT")
    private String imageFile ="default.png";


    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public HouseList() {
    }

    public HouseList(Long houses_id, User_Temporary host_id, List<HouseImage> images, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, String name, String address, String city, Integer numberOfBedrooms, Integer numberOfBathrooms, String description, BigDecimal pricePerDay, HouseListStatusEnum status, String imageFile, BigDecimal latitude, BigDecimal longitude, LocalDateTime updatedAt) {
        this.houses_id = houses_id;
        this.host_id = host_id;
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

    public HouseList(String name, String address, Integer numberOfBedrooms, Integer numberOfBathrooms,
                     String description, BigDecimal pricePerDay, HouseListStatusEnum status, String imageFile) {
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



    public @NotNull(message = "Ngày bắt đầu là bắt buộc") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Ngày bắt đầu là bắt buộc") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "Ngày kết thúc là bắt buộc") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "Ngày kết thúc là bắt buộc") LocalDate endDate) {
        this.endDate = endDate;
    }



    public @NotBlank(message = "Tên của căn nhà là bắt buộc") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Tên của căn nhà là bắt buộc") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Địa chỉ là bắt buộc") String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank(message = "Địa chỉ là bắt buộc") String address) {
        this.address = address;
    }

    public @NotBlank(message = "Thành phố là bắt buộc") String getCity() {
        return city;
    }

    public void setCity(@NotBlank(message = "Thành phố là bắt buộc") String city) {
        this.city = city;
    }

    public @NotNull(message = "Số lượng phòng ngủ là bắt buộc") @Min(value = 1, message = "Số lượng phòng ngủ phải từ 1 đến 10") @Max(value = 10, message = "Số lượng phòng ngủ phải từ 1 đến 10") Integer getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(@NotNull(message = "Số lượng phòng ngủ là bắt buộc") @Min(value = 1, message = "Số lượng phòng ngủ phải từ 1 đến 10") @Max(value = 10, message = "Số lượng phòng ngủ phải từ 1 đến 10") Integer numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public @NotNull(message = "Số lượng phòng tắm là bắt buộc") @Min(value = 1, message = "Số lượng phòng tắm phải từ 1 đến 3") @Max(value = 3, message = "Số lượng phòng tắm phải từ 1 đến 3") Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(@NotNull(message = "Số lượng phòng tắm là bắt buộc") @Min(value = 1, message = "Số lượng phòng tắm phải từ 1 đến 3") @Max(value = 3, message = "Số lượng phòng tắm phải từ 1 đến 3") Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }



    public @NotNull(message = "Giá tiền theo ngày là bắt buộc") BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(@NotNull(message = "Giá tiền theo ngày là bắt buộc") BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public @NotNull(message = "Status là bắt buộc") HouseListStatusEnum getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status là bắt buộc") HouseListStatusEnum status) {
        this.status = status;
    }


    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}
