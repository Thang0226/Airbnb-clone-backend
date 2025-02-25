# Drop tables & run again to create new tables
drop table if exists users_roles;
drop table if exists house_images;
drop table if exists bookings;
drop table if exists availabilities;
drop table if exists houses;
drop table if exists host_requests;
drop table if exists users;
drop table if exists users_temporary;
drop table if exists roles;
use airbnb;



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
           COALESCE(COUNT(DISTINCT h.id), 0) AS housesForRent,
           COALESCE(SUM((DATEDIFF(b.end_date, b.start_date) + 1) * b.price), 0) AS totalIncome
    from users u
             left join houses h on u.id = h.host_id
             left join bookings b on h.id = b.house_id and b.status = 'CHECKED_OUT'
             left join users_roles ur on u.id = ur.user_id
    where ur.roles_id = 2
    GROUP BY u.id;
end;



# Change database collate to case-sensitive comparing with varchar
ALTER TABLE users MODIFY username VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;



# Data
INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_HOST'),
    ('ROLE_ADMIN');

INSERT INTO users
(address, full_name, password, phone, email, username, status)
VALUES
    (
        '12 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh',
        'John Doe',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0123456789',
        'john.d@gmail.com',
        'john_doe',
        'ACTIVE'
    ),
    (
        '25 Trần Duy Hưng, Cầu Giấy, Hà Nội',
        'Sarah Smith',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0987654322',
        'sarah.s@gmail.com',
        'sarah_smith',
        'ACTIVE'
    ),
    (
        '90 Lê Lợi, Quận Hải Châu, Đà Nẵng',
        'Michael Johnson',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0456789123',
        'mike.j@gmail.com',
        'mike_j',
        'ACTIVE'
    ),
    (
        '66 Nguyễn Hữu Thọ, Quận 7, TP. Hồ Chí Minh',
        'Emily Brown',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0789123456',
        'emily.b@gmail.com',
        'emily_b',
        'ACTIVE'
    ),
    (
        '57 Bạch Đằng, Quận Hải Châu, Đà Nẵng',
        'David Wilson',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0321654987',
        'david.w@gmail.com',
        'david_w',
        'ACTIVE'
    ),
    (
        '38 Nguyễn Văn Linh, Quận Hải Châu, Đà Nẵng',
        'Lisa Taylor',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0147258369',
        'lisa.t@gmail.com',
        'lisa_t',
        'ACTIVE'
    ),
    (
        '45 Phan Chu Trinh, Quận Hoàn Kiếm, Hà Nội',
        'Robert Miller',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0258147369',
        'robert.m@gmail.com',
        'robert_m',
        'ACTIVE'
    ),
    (
        '102 Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh',
        'Jennifer Davis',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0369147258',
        'jennifer.d@gmail.com',
        'jennifer_d',
        'ACTIVE'
    ),
    (
        '78 Võ Văn Kiệt, Quận Sơn Trà, Đà Nẵng',
        'William Jones',
        '$2a$12$.NjQ.EJcK8atVQjMaWw5A.JHdu/OtQ6T12Yn6b4xcm9l0HJdXeZ.O', -- "123456"
        '0741852963',
        'william.j@gmail.com',
        'william_j',
        'ACTIVE'
    ),
    (
        '15 Hoàng Hoa Thám, Quận Ninh Kiều, Cần Thơ',
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
        'PENDING',
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
(address, bathrooms, bedrooms, description, house_name, price, host_id, status)
VALUES
    ( '12 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', 2, 3, 'Fully-equipped apartment, centrally located, suitable for couples or small families.', 'Hoàn Kiếm Villa', 800000, 2, 'RENTED'),
    ('25 Trần Duy Hưng, Cầu Giấy, Hà Nội', 3, 4, 'Spacious villa with garden, perfect for large families or groups.', 'Ba Đình Villa', 1500000, 2, 'RENTED'),
    ('90 Lê Lợi, Quận Hải Châu, Đà Nẵng', 2, 2, 'Lake-view apartment with modern design, ideal for expats and digital nomads.', 'Tây Hồ Lakeview Apartment', 900000, 2, 'RENTED'),
    ('15 Hoàng Hoa Thám, Quận Ninh Kiều, Cần Thơ', 1, 1, 'Cozy studio in central Hanoi, close to embassies and business areas.', 'Kim Mã Studio', 600000, 2, 'RENTED'),
    ('78 Võ Văn Kiệt, Quận Sơn Trà, Đà Nẵng', 2, 3, 'Traditional-style home in Hanoi Old Quarter, offering an authentic experience.', 'Old Quarter Charm House', 850000, 2, 'RENTED'),
    ('102 Lý Tự Trọng, Quận 1, TP. Hồ Chí Minh', 2, 2, 'Modern apartment with full amenities, near shopping malls and offices.', 'Láng Hạ Modern Condo', 950000, 3, 'RENTED'),
    ('45 Phan Chu Trinh, Quận Hoàn Kiếm, Hà Nội', 2, 3, 'Newly built apartment with gym and pool access.', 'Thanh Xuân Luxury Condo', 1000000, 3, 'RENTED'),
    ('38 Nguyễn Văn Linh, Quận Hải Châu, Đà Nẵng', 1, 2, 'Quiet and comfortable apartment near West Lake.', 'West Lake Retreat', 700000, 3, 'RENTED'),
    ('57 Bạch Đằng, Quận Hải Châu, Đà Nẵng', 3, 4, 'Spacious home with rooftop terrace, perfect for family gatherings.', ' Đội Cấn Family House', 1200000, 3, 'RENTED'),
    ('66 Nguyễn Hữu Thọ, Quận 7, TP. Hồ Chí Minh', 2, 3, 'Historic French colonial house with elegant decor.', 'French Colonial Residence', 1400000, 3, 'RENTED');

insert into house_images (house_id, file_name)
values  (1, 'hinh anh so (1).jpg'),
        (1, 'hinh anh so (2).jpg'),
        (1, 'hinh anh so (6).jpg'),
        (2, 'hinh anh so (1).jpg'),
        (2, 'hinh anh so (2).jpg'),
        (2, 'hinh anh so (3).jpg'),
        (2, 'hinh anh so (7).jpg'),
        (3, 'hinh anh so (8).jpg'),
        (3, 'hinh anh so (9).jpg'),
        (4, 'hinh anh so (10).jpg'),
        (4, 'hinh anh so (11).jpg'),
        (5, 'hinh anh so (12).jpg'),
        (5, 'hinh anh so (13).jpg'),
        (6, 'hinh anh so (14).jpg'),
        (6, 'hinh anh so (15).jpg'),
        (7, 'hinh anh so (16).jpg'),
        (7, 'hinh anh so (17).jpg'),
        (7, 'hinh anh so (18).jpg'),
        (8, 'hinh anh so (19).jpg'),
        (8, 'hinh anh so (20).jpg'),
        (8, 'hinh anh so (21).jpg'),
        (8, 'hinh anh so (22).jpg'),
        (9, 'hinh anh so (23).jpg'),
        (9, 'hinh anh so (24).jpg'),
        (9, 'hinh anh so (25).jpg'),
        (10, 'hinh anh so (26).jpg'),
        (10, 'hinh anh so (27).jpg');

insert into host_requests (request_date, user_id)
values
    ('2025-02-21 13:07:43.708469', 11),
    ('2025-02-20 13:07:43.708469', 5),
    ('2025-02-09 13:07:43.708469', 6);

insert into availabilities (start_date, end_date, house_id)
values
-- House 1
    ('2025-03-01', '2025-04-15', 1),
    ('2025-05-01', '2025-05-15', 1),
    ('2025-05-31', '2025-05-31', 1),
    ('2025-06-10', '2025-06-30', 1),
    ('2025-07-30', '2027-10-10', 1),

-- House 2
    ('2025-02-15', '2025-03-31', 2),
    ('2025-04-10', '2025-04-30', 2),
    ('2025-05-10', '2025-05-31', 2),
    ('2025-07-01', '2027-10-10', 2),

-- House 3
    ('2025-03-15', '2025-04-30', 3),
    ('2025-06-01', '2025-07-15', 3),
    ('2025-07-31', '2027-10-10',3),

-- House 4
    ('2025-02-01', '2025-02-25', 4),
    ('2025-03-01', '2025-03-15', 4),
    ('2025-05-15', '2025-06-30', 4),
    ('2025-07-15', '2027-10-10', 4),

-- House 5
    ('2025-04-01', '2025-05-31', 5),
    ('2025-07-15', '2025-08-31', 5),
    ('2025-09-30', '2027-10-10', 5),

-- House 6
    ('2025-03-01', '2025-04-30', 6),
    ('2025-06-15', '2025-07-31', 6),
    ('2025-10-31', '2027-10-10', 6),

-- House 7
    ('2025-02-15', '2025-04-15', 7),
    ('2025-05-01', '2025-06-15', 7),
    ('2025-09-30', '2027-10-10', 7),

-- House 8
    ('2025-03-15', '2025-05-15', 8),
    ('2025-06-01', '2025-07-31', 8),
    ('2025-10-15', '2027-10-10', 8),

-- House 9
    ('2025-04-01', '2025-05-31', 9),
    ('2025-07-15', '2025-08-31', 9),
    ('2025-11-15', '2027-10-10', 9),

-- House 10
    ('2025-03-01', '2025-04-30', 10),
    ('2025-06-01', '2025-07-31', 10),
    ('2025-11-30', '2027-10-10', 10);

insert into bookings (start_date, end_date, status, updated_at, price, house_id, user_id)
values
-- House 1
    ('2025-04-16', '2025-04-30', 'WAITING', '2025-03-16 14:30:00',800000, 1, 4),
    ('2025-05-16', '2025-05-30', 'WAITING', '2025-03-16 14:30:00',800000, 1, 4),
    ('2025-06-01', '2025-06-09', 'WAITING', '2025-06-01 09:45:00',800000, 1,5),
    ('2025-07-01', '2025-07-29', 'WAITING', '2025-06-01 09:45:00',800000, 1,5),


-- House 2
    ('2025-04-01', '2025-04-09', 'WAITING', '2025-03-01 11:00:00', 1500000, 2, 6),
    ('2025-05-01', '2025-05-09', 'WAITING', '2025-04-01 11:00:00', 1500000, 2, 6),
    ('2025-06-01', '2025-06-30', 'WAITING', '2025-05-01 13:15:00', 1500000, 2, 7),

-- House 3 (gaps: May 1-May 31, Jul 16-Oct 30)
    ('2025-05-01', '2025-05-31', 'WAITING', '2025-04-01 15:45:00', 900000, 3, 8),
    ('2025-07-16', '2025-07-30', 'WAITING', '2025-06-16 12:00:00', 900000, 3, 9),

-- House 4 (gaps: Mar 16-May 14, Jul 1-Sep 14)
    ('2025-02-26', '2025-02-28', 'WAITING', '2025-02-16 14:20:00', 600000, 4, 10),
    ('2025-03-16', '2025-05-14', 'WAITING', '2025-02-16 14:20:00', 600000, 4, 10),
    ('2025-07-01', '2025-07-14', 'WAITING', '2025-06-01 16:45:00', 600000, 4, 10),

-- House 5 (gaps: Jun 1-Jul 14, Sep 1-Nov 29)
    ('2025-06-01', '2025-07-14', 'WAITING', '2025-05-01 10:15:00', 850000, 5, 4),
    ('2025-09-01', '2025-09-29', 'WAITING', '2025-08-01 13:30:00', 850000, 5, 5),

-- House 6 (gaps: May 1-Jun 14, Aug 1-Oct 30)
    ('2025-05-01', '2025-06-14', 'WAITING', '2025-04-01 11:20:00', 950000, 6, 6),
    ('2025-08-01', '2025-10-30', 'WAITING', '2025-07-01 15:00:00', 950000, 6, 7),

-- House 7 (gaps: Apr 16-Apr 30, Jun 16-Sep 29)
    ('2025-04-16', '2025-04-30', 'WAITING', '2025-03-16 09:00:00', 1000000, 7, 8),
    ('2025-06-16', '2025-09-29', 'WAITING', '2025-05-16 14:45:00', 1000000, 7, 9),

-- House 8 (gaps: May 16-May 31, Aug 1-Oct 14)
    ('2025-05-16', '2025-05-31', 'WAITING', '2025-04-16 16:15:00', 700000, 8, 10),
    ('2025-08-01', '2025-10-14', 'WAITING', '2025-07-01 10:45:00', 700000, 8, 4),

-- House 9 (gaps: Jun 1-Jul 14, Sep 1-Nov 14)
    ('2025-06-01', '2025-07-14', 'WAITING', '2025-05-01 11:30:00', 1200000, 9, 5),
    ('2025-09-01', '2025-09-14', 'WAITING', '2025-08-01 15:45:00', 1200000, 9, 6),

-- House 10 (gaps: May 1-May 31, Aug 1-Nov 29)
    ('2025-05-01', '2025-05-31', 'WAITING', '2025-04-01 14:00:00', 1400000, 10, 11),
    ('2025-08-01', '2025-08-29', 'WAITING', '2025-07-01 10:30:00', 1400000, 10, 11);