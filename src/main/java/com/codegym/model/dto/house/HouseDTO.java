package com.codegym.model.dto.house;

import com.codegym.model.HouseImage;
import com.codegym.model.constants.HouseStatus;
import lombok.Data;

import java.util.List;

@Data
public class HouseDTO {
    private Long id;
    private String houseName;
    private String address;
    private int bedrooms;
    private int bathrooms;
    private String description;
    private int price;
    private HouseStatus status;
    private Integer rentals;
    private List<HouseImage> houseImages;
//    private User host;
}
