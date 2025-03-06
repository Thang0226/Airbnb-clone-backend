# Drop tables & run again to create new tables
drop table if exists users_roles;
drop table if exists houses_house_images;
drop table if exists house_images;
drop table if exists reviews;
drop table if exists bookings;
drop table if exists availabilities;
drop table if exists house_maintenance;
drop table if exists houses;
drop table if exists host_requests;
drop table if exists notifications;
drop table if exists users;
drop table if exists users_temporary;
drop table if exists roles;
use airbnb;



# Change database collate to case-sensitive comparing with varchar
ALTER TABLE users MODIFY username VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;



# Procedures
drop procedure if exists search_houses_asc;
create procedure search_houses_asc(
    IN address VARCHAR(255),
    IN checkIn DATE,
    IN checkOut DATE,
    IN minBedrooms INT,
    IN minBathrooms INT,
    IN minPrice INT,
    IN maxPrice INT
)
begin
    SELECT h.*
    FROM houses h
             LEFT JOIN availabilities a ON a.house_id = h.id
    WHERE ( minBedrooms IS NULL OR h.bedrooms >= minBedrooms )
      AND ( minBathrooms IS NULL OR h.bathrooms >= minBathrooms )
      AND ( address IS NULL OR TRIM(address) = '' OR LOWER(h.address) LIKE CONCAT('%', LOWER(address), '%'))
      AND ( checkIn IS NULL OR (a.start_date <= checkIn AND a.end_date >= checkOut) )
      AND ( minPrice IS NULL OR h.price >= minPrice )
      AND ( maxPrice IS NULL OR h.price <= maxPrice )
    ORDER BY h.price;
end;

drop procedure if exists search_houses_desc;
create procedure search_houses_desc(
    IN address VARCHAR(255),
    IN checkIn DATE,
    IN checkOut DATE,
    IN minBedrooms INT,
    IN minBathrooms INT,
    IN minPrice INT,
    IN maxPrice INT
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
    ORDER BY h.price DESC;
end;

drop procedure if exists get_host_info;
create procedure get_host_info(in _id bigint)
begin
    select u.id as id,
           u.username as username,
           u.avatar as avatar,
           u.status as status,
           u.full_name as fullName,
           u.address as address,
           u.phone as phone,
           u.email as email,
           COALESCE(COUNT(DISTINCT h.id), 0) AS housesForRent,
           COALESCE(SUM((DATEDIFF(b.end_date, b.start_date) + 1) * b.price), 0) AS totalIncome
    from users u
    left join houses h on u.id = h.host_id
    left join bookings b on h.id = b.house_id and b.status = 'CHECKED_OUT'
    left join users_roles ur on u.id = ur.user_id
    where u.id = _id and ur.roles_id = 2
    GROUP BY u.id;
end;
call get_host_info(2);

drop procedure if exists get_all_hosts_info;
create procedure get_all_hosts_info()
begin
    select u.id as id,
           u.username as username,
           u.status as status,
           u.full_name as fullName,
           u.address as address,
           u.phone as phone,
           u.email as email,
           COALESCE(COUNT(DISTINCT h.id), 0) AS housesForRent,
           COALESCE(SUM((DATEDIFF(b.end_date, b.start_date) + 1) * b.price), 0) AS totalIncome
    from users u
             left join houses h on u.id = h.host_id
             left join bookings b on h.id = b.house_id and b.status = 'CHECKED_OUT'
             left join users_roles ur on u.id = ur.user_id
    where ur.roles_id = 2
    GROUP BY u.id;
end;

DROP PROCEDURE IF EXISTS get_host_income_by_month;
CREATE PROCEDURE get_host_income_by_month(
    IN _username VARCHAR(255),
    IN _numMonths INT
)
BEGIN
    -- Create a temporary table to hold all months in the range, but have to use DATE type
    -- cause SQL do not have MONTH type
    DROP TEMPORARY TABLE IF EXISTS month_range;
    CREATE TEMPORARY TABLE month_range (month_date DATE);
    SET @counter := 0;
    WHILE @counter < _numMonths DO
            INSERT INTO month_range (month_date)
            VALUES (DATE_SUB(DATE_FORMAT(CURRENT_DATE(), '%Y-%m-01'), INTERVAL @counter MONTH));
            SET @counter := @counter + 1;
        END WHILE;
#     select * from month_range;

    SELECT
        COALESCE(SUM(b.price), 0) AS total_income
    FROM
        month_range mr
            LEFT JOIN (
            SELECT
                b.updated_at,
                b.price
            FROM
                bookings b
                    JOIN
                houses h ON b.house_id = h.id
                    JOIN
                users u ON h.host_id = u.id
            WHERE
                u.username = _username
              AND b.status = 'CHECKED_OUT'
        ) AS b ON DATE_FORMAT(b.updated_at, '%Y-%m') = DATE_FORMAT(mr.month_date, '%Y-%m')
    GROUP BY
        DATE_FORMAT(mr.month_date, '%Y-%m')
    ORDER BY
        DATE_FORMAT(mr.month_date, '%Y-%m');

    DROP TEMPORARY TABLE IF EXISTS month_range;
END;
call get_host_income_by_month('mike_j', 12);

DROP PROCEDURE IF EXISTS get_host_income_by_year;
CREATE PROCEDURE get_host_income_by_year(
    IN _username VARCHAR(255),
    IN _numYears INT
)
BEGIN
    -- Create a temporary table to hold all years in the range, but have to use DATE type
    -- cause SQL do not have YEAR type
    DROP TEMPORARY TABLE IF EXISTS year_range;
    CREATE TEMPORARY TABLE year_range (year_date DATE);
    SET @counter := 0;
    WHILE @counter < _numYears DO
            INSERT INTO year_range (year_date)
            VALUES (DATE_SUB(DATE_FORMAT(CURRENT_DATE(), '%Y-01-01'), INTERVAL @counter YEAR));
            SET @counter := @counter + 1;
        END WHILE;
#     select * from year_range;

    SELECT
        COALESCE(SUM(b.price), 0) AS total_income
    FROM
        year_range yr
            LEFT JOIN (
            SELECT
                b.updated_at,
                b.price
            FROM
                bookings b
                    JOIN
                houses h ON b.house_id = h.id
                    JOIN
                users u ON h.host_id = u.id
            WHERE
                u.username = _username
              AND b.status = 'CHECKED_OUT'
        ) AS b ON YEAR(b.updated_at) = YEAR(yr.year_date)
    GROUP BY
        YEAR(yr.year_date)
    ORDER BY
        YEAR(yr.year_date);

    DROP TEMPORARY TABLE IF EXISTS year_range;
END;
call get_host_income_by_year('sarah_smith', 5);

drop procedure if exists search_bookings_of_host;
create procedure search_bookings_of_host(
    in _id bigint,
    in _house_name varchar(255),
    in _start_date date,
    in _end_date date,
    in _status varchar(255)
)
begin
    SET _house_name = NULLIF(_house_name, '');
    SET _status = NULLIF(_status, '');
    SELECT
        b.id,
        b.end_date,
        b.start_date,
        b.status,
        b.updated_at,
        b.house_id,
        b.user_id,
        b.price,
        b.created_date
    FROM bookings b
    JOIN houses h on b.house_id = h.id
    WHERE (_house_name IS NULL OR TRIM(_house_name) = '' OR LOWER(h.house_name) LIKE LOWER(CONCAT('%', _house_name, '%')))
        AND (_start_date IS NULL OR b.start_date >= _start_date)
        AND (_end_date IS NULL OR b.end_date <= _end_date)
        and (_status is null or b.status = _status)
        and (h.host_id = _id)
    ORDER BY b.id DESC;
end;

drop procedure if exists get_host_houses_list;
create procedure get_host_houses_list(
    in _id bigint,
    IN _limit INT,
    IN _offset INT
)
begin
    select
        h.id as id,
        h.house_name as houseName,
        h.price as price,
        h.address as address,
        h.rentals as rentals,
        COALESCE(SUM((DATEDIFF(b.end_date, b.start_date) + 1) * b.price), 0) AS totalRevenue,
        h.status as status
    from houses h
        left join bookings b on h.id = b.house_id and b.status = 'CHECKED_OUT'
    where h.host_id = _id
    GROUP BY h.id
    LIMIT _limit OFFSET _offset;
end;

drop procedure if exists search_host_houses;
create procedure search_host_houses(
    in _id bigint,
    in _house_name varchar(255),
    in _status varchar(255),
    IN _limit INT,
    IN _offset INT
)
begin
    SET _house_name = NULLIF(_house_name, '');
    SET _status = NULLIF(_status, '');
    select
        h.id as id,
        h.house_name as houseName,
        h.price as price,
        h.address as address,
        COALESCE(SUM((DATEDIFF(b.end_date, b.start_date) + 1) * b.price), 0) AS totalRevenue,
        h.status as status
    from houses h
             left join bookings b on h.id = b.house_id and b.status = 'CHECKED_OUT'
    where
        (_house_name IS NULL OR TRIM(_house_name) = '' OR LOWER(h.house_name) LIKE LOWER(CONCAT('%', _house_name, '%')))
        and (_status is null or h.status = _status)
        and h.host_id = _id
    GROUP BY h.id
    LIMIT _limit OFFSET _offset;
end;


# Data
INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_HOST'),
    ('ROLE_ADMIN');

INSERT INTO users
(address, avatar, full_name, password, phone, email, username, status)
VALUES
    (
        '12 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh',
        'ava1.png',
        'John Doe',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0123456789',
        'john.d@gmail.com',
        'john_doe',
        'ACTIVE'
    ),
    (
        '25 Trần Duy Hưng, Cầu Giấy, Hà Nội',
        'ava5.png',
        'Sarah Smith',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0987654322',
        'sarah.s@gmail.com',
        'sarah_smith',
        'ACTIVE'
    ),
    (
        '90 Lê Lợi, Quận Hải Châu, Đà Nẵng',
        'ava2.png',
        'Michael Johnson',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0456789123',
        'mike.j@gmail.com',
        'mike_j',
        'ACTIVE'
    ),
    (
        '66 Nguyễn Hữu Thọ, Quận 7, TP. Hồ Chí Minh',
        'ava6.png',
        'Emily Brown',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0789123456',
        'emily.b@gmail.com',
        'emily_b',
        'ACTIVE'
    ),
    (
        '57 Bạch Đằng, Quận Hải Châu, Đà Nẵng',
        'ava3.png',
        'David Wilson',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0321654987',
        'david.w@gmail.com',
        'david_w',
        'ACTIVE'
    ),
    (
        '38 Nguyễn Văn Linh, Quận Hải Châu, Đà Nẵng',
        'ava8.png',
        'Lisa Taylor',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0147258369',
        'lisa.t@gmail.com',
        'lisa_t',
        'ACTIVE'
    ),
    (
        '45 Phan Chu Trinh, Quận Hoàn Kiếm, Hà Nội',
        'ava4.png',
        'Robert Miller',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0258147369',
        'robert.m@gmail.com',
        'robert_m',
        'ACTIVE'
    ),
    (
        '102 Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh',
        null,
        'Jennifer Davis',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0369147258',
        'jennifer.d@gmail.com',
        'jennifer_d',
        'ACTIVE'
    ),
    (
        '78 Võ Văn Kiệt, Quận Sơn Trà, Đà Nẵng',
        'ava7.png',
        'William Jones',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0741852963',
        'william.j@gmail.com',
        'william_j',
        'ACTIVE'
    ),
    (
        '15 Hoàng Hoa Thám, Quận Ninh Kiều, Cần Thơ',
        null,
        'Mary Anderson',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0963852741',
        'mary.a@gmail.com',
        'mary_a',
        'LOCKED'
    );

INSERT INTO users
(address, full_name, password, phone, email, username, status, is_gg_account)
values
    (
        'Số 1 Ngô Xuân Quảng, Trâu Quỳ, Gia Lâm, Hà Nội',
        'Nguyễn Đức Thắng',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0888899999',
        'thang.nd0226@gmail.com',
        'Thắng Nguyễn Đức',
        'ACTIVE',
        true
    );


INSERT INTO users_roles
(user_id, roles_id)
VALUES
    (1, 3),
    (2, 2),
    (3, 2),
    (4, 1),
    (5, 1),
    (6, 1),
    (7, 1),
    (8, 1),
    (9, 1),
    (10, 1),
    (11, 1);

INSERT INTO houses
(address, bathrooms, bedrooms, description, house_name, price, host_id, status, rentals)
VALUES
    ( '12 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', 2, 3, 'Fully-equipped apartment, centrally located, suitable for couples or small families.', 'Hoàn Kiếm Villa', 800000, 2, 'RENTED', 1),
    ('25 Trần Duy Hưng, Cầu Giấy, Hà Nội', 3, 4, 'Spacious villa with garden, perfect for large families or groups.', 'Ba Đình Villa', 1500000, 2, 'RENTED', 3),
    ('90 Lê Lợi, Quận Hải Châu, Đà Nẵng', 2, 2, 'Lake-view apartment with modern design, ideal for expats and digital nomads.', 'Tây Hồ Lakeview Apartment', 900000, 2, 'RENTED', 2),
    ('15 Hoàng Hoa Thám, Quận Ninh Kiều, Cần Thơ', 1, 1, 'Cozy studio in central Hanoi, close to embassies and business areas.', 'Kim Mã Studio', 600000, 2, 'RENTED', 2),
    ('78 Võ Văn Kiệt, Quận Sơn Trà, Đà Nẵng', 2, 3, 'Traditional-style home in Hanoi Old Quarter, offering an authentic experience.', 'Old Quarter Charm House', 850000, 2, 'RENTED', 2),
    ('102 Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh', 2, 2, 'Modern apartment with full amenities, near shopping malls and offices.', 'Láng Hạ Modern Condo', 950000, 3, 'RENTED', 1),
    ('45 Phan Chu Trinh, Quận Hoàn Kiếm, Hà Nội', 2, 3, 'Newly built apartment with gym and pool access.', 'Thanh Xuân Luxury Condo', 1000000, 3, 'AVAILABLE', 1),
    ('38 Nguyễn Văn Linh, Quận Hải Châu, Đà Nẵng', 1, 2, 'Quiet and comfortable apartment near West Lake.', 'West Lake Retreat', 700000, 3, 'RENTED', 1),
    ('57 Bạch Đằng, Quận Hải Châu, Đà Nẵng', 3, 4, 'Spacious home with rooftop terrace, perfect for family gatherings.', ' Đội Cấn Family House', 1200000, 3, 'RENTED', 1),
    ('66 Nguyễn Hữu Thọ, Quận 7, TP. Hồ Chí Minh', 2, 3, 'Historic French colonial house with elegant decor.', 'French Colonial Residence', 1400000, 3, 'RENTED', 1);

insert into house_images (file_name, house_id)
values  ('hinh anh so (1).jpg', 1),
        ('hinh anh so (2).jpg', 1),
        ('hinh anh so (3).jpg', 1),
        ('hinh anh so (4).jpg', 2),
        ('hinh anh so (5).jpg', 2),
        ('hinh anh so (6).jpg', 2),
        ('hinh anh so (7).jpg', 2),
        ('hinh anh so (8).jpg', 3),
        ('hinh anh so (9).jpg', 3),
        ('hinh anh so (10).jpg', 4),
        ('hinh anh so (11).jpg', 4),
        ('hinh anh so (12).jpg', 5),
        ('hinh anh so (13).jpg', 5),
        ('hinh anh so (14).jpg', 6),
        ('hinh anh so (15).jpg', 6),
        ('hinh anh so (16).jpg',7),
        ('hinh anh so (17).jpg', 7),
        ('hinh anh so (18).jpg', 7),
        ('hinh anh so (19).jpg', 8),
        ('hinh anh so (20).jpg', 8),
        ('hinh anh so (21).jpg', 8),
        ('hinh anh so (22).jpg', 8),
        ('hinh anh so (23).jpg', 9),
        ('hinh anh so (24).jpg', 9),
        ('hinh anh so (25).jpg', 9),
        ('hinh anh so (26).jpg', 10),
        ('hinh anh so (30).jpg', 10),
        ('hinh anh so (31).jpg', 10);

insert into host_requests (request_date, user_id)
values
    ('2025-02-21 13:07:43.708469', 10),
    ('2025-02-20 13:07:43.708469', 5),
    ('2025-02-09 13:07:43.708469', 6);

INSERT INTO availabilities (start_date, end_date, house_id)
VALUES
    ('2023-01-01', '2024-06-30', 1),
    ('2024-07-30', '2025-04-15', 1),
    ('2025-05-01', '2025-05-15', 1),
    ('2025-05-31', '2025-05-31', 1),
    ('2025-06-10', '2027-12-31', 1),

    ('2023-01-01', '2024-03-31', 2),
    ('2024-04-10', '2024-04-30', 2),
    ('2024-05-10', '2024-05-31', 2),
    ('2024-07-01', '2027-12-31', 2),

    ('2023-01-01', '2024-04-30', 3),
    ('2024-06-01', '2024-07-15', 3),
    ('2024-07-31', '2027-12-31', 3),

    ('2023-01-01', '2024-03-15', 4),
    ('2024-05-15', '2024-06-30', 4),
    ('2024-07-15', '2025-02-25', 4),
    ('2025-03-01', '2027-12-31', 4),

    ('2023-01-01', '2024-05-31', 5),
    ('2024-06-15', '2024-08-31', 5),
    ('2024-09-30', '2027-12-31', 5),

    ('2023-01-01', '2024-09-30', 6),
    ('2024-10-31', '2025-04-30', 6),
    ('2025-06-15', '2027-12-31', 6),

    ('2023-01-01', '2024-09-15', 7),
    ('2024-09-30', '2025-04-15', 7),
    ('2025-05-01', '2027-12-31', 7),

    ('2023-01-01', '2024-09-30', 8),
    ('2024-10-15', '2025-05-15', 8),
    ('2025-06-01', '2027-12-31', 8),

    ('2023-01-01', '2024-08-31', 9),
    ('2024-09-15', '2025-05-31', 9),
    ('2025-06-15', '2027-12-31', 9),

    ('2023-01-01', '2024-07-31', 10),
    ('2024-08-30', '2025-04-30', 10),
    ('2025-06-01', '2027-12-31', 10);

INSERT INTO bookings (start_date, end_date, status, updated_at, price, house_id, user_id)
VALUES
    ('2024-07-01', '2024-07-29', 'CHECKED_OUT', '2024-07-29 14:30:00', 23200000, 1, 5),
    ('2025-04-16', '2025-04-30', 'WAITING', '2025-02-16 14:30:00', 11200000, 1, 4),
    ('2025-05-16', '2025-05-30', 'WAITING', '2025-02-17 14:30:00', 11200000, 1, 4),
    ('2025-06-01', '2025-06-09', 'WAITING', '2025-02-18 14:30:00', 6400000, 1, 5),

    ('2024-04-01', '2024-04-09', 'CHECKED_OUT', '2024-04-09 14:30:00', 12000000, 2, 6),
    ('2024-05-01', '2024-05-09', 'CHECKED_OUT', '2024-05-09 14:30:00', 12000000, 2, 6),
    ('2024-06-01', '2024-06-30', 'CHECKED_OUT', '2024-06-30 14:30:00', 43500000, 2, 7),

    ('2024-05-01', '2024-05-31', 'CHECKED_OUT', '2024-05-31 14:30:00', 27000000, 3, 8),
    ('2024-07-16', '2024-07-30', 'CHECKED_OUT', '2024-07-30 14:30:00', 12600000, 3, 9),

    ('2024-03-16', '2024-05-14', 'CHECKED_OUT', '2024-05-14 14:30:00', 36000000, 4, 10),
    ('2024-07-01', '2024-07-14', 'CHECKED_OUT', '2024-07-14 14:30:00', 8400000, 4, 10),
    ('2025-02-26', '2025-02-28', 'CHECKED_IN', '2024-02-26 14:30:00', 1200000, 4, 10),

    ('2024-06-01', '2024-06-14', 'CHECKED_OUT', '2024-06-14 14:30:00', 35700000, 5, 4),
    ('2024-09-01', '2024-09-29', 'CHECKED_OUT', '2024-09-29 14:30:00', 23800000, 5, 5),

    ('2024-10-01', '2024-10-30', 'CHECKED_OUT', '2024-10-30 14:30:00', 85500000, 6, 7),
    ('2025-05-01', '2025-06-14', 'WAITING', '2025-02-16 14:30:00', 42750000, 6, 6),

    ('2024-09-16', '2024-09-29', 'CHECKED_OUT', '2024-09-29 14:30:00', 106000000, 7, 9),
    ('2025-04-16', '2025-04-30', 'WAITING', '2025-02-17 14:30:00', 15000000, 7, 8),

    ('2024-10-01', '2024-10-14', 'CHECKED_OUT', '2024-10-14 14:30:00', 51800000, 8, 4),
    ('2025-05-16', '2025-05-31', 'WAITING', '2025-02-18 14:30:00', 10500000, 8, 10),

    ('2024-09-01', '2024-09-14', 'CHECKED_OUT', '2024-09-14 14:30:00', 16800000, 9, 6),
    ('2025-06-01', '2025-06-14', 'WAITING', '2025-02-14 14:30:00', 51600000, 9, 5),

    ('2024-08-01', '2024-08-29', 'CHECKED_OUT', '2024-08-29 14:30:00', 40600000, 10, 11),
    ('2025-05-01', '2025-05-31', 'WAITING', '2025-02-20 14:30:00', 42000000, 10, 11);

INSERT INTO notifications (created_at, message, host_id)
VALUES
    ('2025-02-16 14:30:00', '"Lisa Taylor" BOOKED the house "Hoàn Kiếm Villa" on 16/02/2025', 2),
    ('2025-02-17 14:30:00', '"Lisa Taylor" BOOKED the house "Hoàn Kiếm Villa" on 17/02/2025', 2),
    ('2025-02-18 14:30:00', '"David Wilson" BOOKED the house "Hoàn Kiếm Villa" on 18/02/2025', 2),
    ('2025-02-16 14:30:00', '"William Jones" BOOKED the house "Láng Hạ Modern Condo" on 16/02/2025', 3),
    ('2025-02-17 14:30:00', '"Jennifer Davis" BOOKED the house "Thanh Xuân Luxury Condo" on 17/02/2025', 3),
    ('2025-02-18 14:30:00', '"Mary Anderson" BOOKED the house "West Lake Retreat" on 18/02/2025', 3),
    ('2025-02-14 14:30:00', '"Lisa Taylor" BOOKED the house "Đội Cấn Family House" on 14/02/2025', 3),
    ('2025-02-20 14:30:00', '"Jennifer Davis" BOOKED the house "French Colonial Residence" on 20/02/2025', 3),
    ('2024-07-30 14:30:00', '"David Wilson" reviewed the house "Hoàn Kiếm Villa" on 30/07/2024', 2),
    ('2024-04-10 14:30:00', '"Emily Brown" reviewed the house "Ba Đình Villa" on 10/04/2024', 2),
    ('2024-05-10 14:30:00', '"Michael Johnson" reviewed the house "Tây Hồ Lakeview Apartment" on 10/05/2024', 2),
    ('2024-07-02 14:30:00', '"Jennifer Davis" reviewed the house "Kim Mã Studio" on 02/07/2024', 2),
    ('2024-06-01 14:30:00', '"William Jones" reviewed the house "Old Quarter Charm House" on 01/06/2024', 2),
    ('2024-07-31 14:30:00', '"Sarah Smith" reviewed the house "Láng Hạ Modern Condo" on 31/07/2024', 3),
    ('2024-05-15 14:30:00', '"Robert Miller" reviewed the house "Thanh Xuân Luxury Condo" on 15/05/2024', 3),
    ('2024-07-15 14:30:00', '"Lisa Taylor" reviewed the house "West Lake Retreat" on 15/07/2024', 3),
    ('2024-06-15 14:30:00', '"John Doe" reviewed the house "Đội Cấn Family House" on 15/06/2024', 3),
    ('2024-09-30 14:30:00', '"Mary Anderson" reviewed the house "French Colonial Residence" on 30/09/2024', 3),
    ('2024-10-31 14:30:00', '"Sarah Smith" reviewed the house "Hoàn Kiếm Villa" on 31/10/2024', 2),
    ('2024-09-30 14:30:00', '"David Wilson" reviewed the house "Ba Đình Villa" on 30/09/2024', 2),
    ('2024-10-15 14:30:00', '"Michael Johnson" reviewed the house "Tây Hồ Lakeview Apartment" on 15/10/2024', 2),
    ('2024-09-15 14:30:00', '"Lisa Taylor" reviewed the house " Đội Cấn Family House" on 15/09/2024', 3),
    ('2024-08-30 14:30:00', '"Thắng Nguyễn Đức" reviewed the house "French Colonial Residence" on 30/08/2024', 3);

INSERT INTO reviews (comment, rating, updated_at, booking_id, is_hidden)
VALUES
    ('Absolutely loved our stay! The house was even more beautiful than in the pictures.', 5, '2024-07-30', 1, 0),
    ('Great location and comfortable amenities. Would recommend!', 4, '2024-04-10', 5, 0),
    ('Nice property but some maintenance issues in the bathroom.', 3, '2024-05-10', 6, 0),
    ('Perfect for our family vacation. Spacious and well-equipped kitchen.', 5, '2024-07-02', 7, 0),
    ('Enjoyed the peaceful surroundings. Host was very responsive.', 4, '2024-06-01', 8, 0),
    ('Amazing views and convenient location to local attractions.', 5, '2024-07-31', 9, 0),
    ('Property was clean but smaller than it appeared in photos.', 3, '2024-05-15', 10, 0),
    ('We had a wonderful time! The backyard was perfect for our kids.', 4, '2024-07-15', 11, 0),
    ('Stylish interior and very comfortable beds. Would stay again!', 5, '2024-06-15', 13, 0),
    ('Great value for money. Everything was as described.', 4, '2024-09-30', 14, 0),
    ('Luxury at its finest! Worth every penny for a special occasion.', 5, '2024-10-31', 15, 0),
    ('The property exceeded our expectations. Beautiful neighborhood.', 5, '2024-09-30', 17, 0),
    ('Modern, clean, and the host was exceptionally accommodating.', 4, '2024-10-15', 19, 0),
    ('Cozy place with charming details. Minor issue with WiFi but otherwise great.', 4, '2024-09-15', 21, 0),
    ('Perfect getaway! Loved the pool and outdoor entertainment area.', 5, '2024-08-30', 23, 0);