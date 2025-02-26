package com.codegym.repository;

import com.codegym.model.House;
import com.codegym.model.dto.house.HouseListDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IHouseRepository extends JpaRepository<House, Long> {

  @Query(nativeQuery = true, value = "call search_houses_asc(:address, :checkIn, :checkOut, :minBedrooms, :minBathrooms, :minPrice, :maxPrice)")
  List<House> searchHousesAsc(
          @Param("address") String address,
          @Param("checkIn") LocalDate checkIn,
          @Param("checkOut") LocalDate checkOut,
          @Param("minBedrooms") Integer minBedrooms,
          @Param("minBathrooms") Integer minBathrooms,
          @Param("minPrice") Integer minPrice,
          @Param("maxPrice") Integer maxPrice);

  @Query(nativeQuery = true, value = "call search_houses_desc(:address, :checkIn, :checkOut, :minBedrooms, :minBathrooms, :minPrice, :maxPrice)")
  List<House> searchHousesDesc(
          @Param("address") String address,
          @Param("checkIn") LocalDate checkIn,
          @Param("checkOut") LocalDate checkOut,
          @Param("minBedrooms") Integer minBedrooms,
          @Param("minBathrooms") Integer minBathrooms,
          @Param("minPrice") Integer minPrice,
          @Param("maxPrice") Integer maxPrice);

  List<House> findHousesByHost_Id(Long hostId);

  @Query(nativeQuery = true, value = "call get_host_houses_list(:hostId, :limit, :offset)")
  List<HouseListDTO> findHouseListByHostId( @Param("hostId") Long hostId,
                                            @Param("limit") int limit,
                                            @Param("offset") int offset
  );

  @Query(nativeQuery = true, value = "call search_host_houses(:hostId, :houseName, :status, :limit, :offset)")
  List<HouseListDTO> searchHostHouse( @Param("hostId") Long hostId,
                                      @Param("houseName") String houseName,
                                      @Param("status") String status,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset
  );
}
