package com.codegym.service.houseImage;

import com.codegym.model.HouseImage;
import com.codegym.service.IGenerateService;

import java.util.List;

public interface IHouseImageService extends IGenerateService<HouseImage> {
    List<HouseImage> findAllByHouse_Id(Long houseId);
}
