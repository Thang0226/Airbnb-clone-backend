package com.codegym.mapper;


import com.codegym.model.House;
import com.codegym.model.dto.house.TopFiveHousesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface HouseMapper {
    @Mapping(target = "image", source = "house", qualifiedByName = "getFirstImage")
    TopFiveHousesDTO toTopFiveHousesDTO(House house);

    @Named("getFirstImage")
    default String getFirstImage(House house) {
        return (house.getHouseImages() != null && !house.getHouseImages().isEmpty())
                ? house.getHouseImages().get(0).getFileName()
                : "default.png";
    }
}
