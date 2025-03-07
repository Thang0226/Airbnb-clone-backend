package com.codegym.mapper;

import com.codegym.model.House;
import com.codegym.model.HouseImage;
import com.codegym.model.dto.house.HouseDTO;
import com.codegym.model.dto.house.TopFiveHousesDTO;
import com.codegym.repository.IHouseImageRepository;
import com.codegym.service.houseImage.HouseImageService;
import com.codegym.service.houseImage.IHouseImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IHouseImageRepository.class})
public abstract class HouseMapper {
    @Autowired
    protected IHouseImageService imageService;
    @Autowired
    private HouseImageService houseImageService;

    @Mapping(target = "image", source = "house", qualifiedByName = "getFirstImage")
    public abstract TopFiveHousesDTO toTopFiveHousesDTO(House house);

    @Named("getFirstImage")
    String getFirstImage(House house) {
        List<HouseImage> images = imageService.findAllByHouse_Id(house.getId());
        return images.isEmpty() ? "default.png" : images.get(0).getFileName();
    }

    public HouseDTO toHouseDTO(House house) {
        HouseDTO houseDTO = new HouseDTO();
        BeanUtils.copyProperties(house, houseDTO);
        List<HouseImage> images = imageService.findAllByHouse_Id(house.getId());
        houseDTO.setHouseImages(images);
        return houseDTO;
    }
}
