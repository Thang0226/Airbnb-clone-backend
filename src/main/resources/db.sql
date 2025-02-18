DROP PROCEDURE IF EXISTS get_user_profile_by_username;
create procedure get_user_profile_by_username(in _username varchar(255))
begin
    select u.username as username, u.avatar as avatar, u.full_name as fullName, u.address as address, u.phone as phone
    from users u
    where u.username = _username;
end;


drop procedure if exists search_houses;
create procedure search_houses(
    IN address VARCHAR(255),
    IN checkIn DATE,
    IN checkOut DATE,
    IN minBedrooms INT,
    IN minBathrooms INT,
    IN minPrice INT,
    IN maxPrice INT,
    IN priceOrder VARCHAR(10)
)
begin
    SELECT h.*
    FROM houses h
             LEFT JOIN availabilities a ON a.house_id = h.id
    WHERE ( minBedrooms IS NULL OR h.bedrooms >= minBedrooms )
      AND ( minBathrooms IS NULL OR h.bathrooms >= minBathrooms )
      AND ( address IS NULL OR TRIM(address) = '' OR LOWER(h.address) LIKE LOWER(CONCAT('%', address, '%')) )
      AND ( checkIn IS NULL OR (a.start_date <= checkIn AND a.end_date >= checkOut) )
      AND ( minPrice IS NULL OR h.price >= minPrice )
      AND ( maxPrice IS NULL OR h.price <= maxPrice )
    ORDER BY priceOrder;
end;



# Change database collate to case-sensitive comparing with varchar
ALTER TABLE users MODIFY username VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
