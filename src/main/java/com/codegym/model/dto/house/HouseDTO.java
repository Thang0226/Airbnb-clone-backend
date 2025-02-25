package com.codegym.model.dto.house;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class HouseDTO {
     private String houseName;
     private String address;
     private Integer bedrooms;
     private Integer bathrooms;
     private String description;
     private Integer price;
     private List<MultipartFile> houseImages;
     private String username;
}
