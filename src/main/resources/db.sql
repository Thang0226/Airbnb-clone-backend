# Drop tables & run again to create new tables
drop table if exists users_roles;
drop table if exists house_images;
drop table if exists bookings;
drop table if exists availabilities;
drop table if exists houses;
drop table if exists users;
drop table if exists users_temporary;
drop table if exists roles;
use airbnb;



# Procedures
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



# Data
INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_HOST'),
    ('ROLE_ADMIN');

INSERT INTO users
(address, avatar, full_name, password, phone, username)
VALUES
    (
        '123 Oak Street, Austin, TX 78701',
        'default.jpg',
        'John Doe',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0123456789',
        'john_doe'
    ),
    (
        '456 Pine Ave, Seattle, WA 98101',
        'default.jpg',
        'Sarah Smith',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0987654321',
        'sarah_smith'
    ),
    (
        '789 Maple Dr, Chicago, IL 60601',
        'default.jpg',
        'Michael Johnson',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0456789123',
        'mike_j'
    ),
    (
        '321 Elm Road, Miami, FL 33101',
        'default.jpg',
        'Emily Brown',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0789123456',
        'emily_b'
    ),
    (
        '654 Cedar Lane, Denver, CO 80201',
        'default.jpg',
        'David Wilson',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0321654987',
        'david_w'
    ),
    (
        '987 Birch Blvd, Portland, OR 97201',
        'default.jpg',
        'Lisa Taylor',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0147258369',
        'lisa_t'
    ),
    (
        '147 Walnut St, Boston, MA 02101',
        'default.jpg',
        'Robert Miller',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewopWz8T5/zLWXe6', -- "123456"
        '0258147369',
        'robert_m'
    ),
    (
        '258 Spruce Court, Houston, TX 77001',
        'default.jpg',
        'Jennifer Davis',
        '$2a$12$93pjT2iLWasrz1U.8S1fzOAWca/BGdFbnBLx8rmOycAhaqmzn76Uq', -- "123456"
        '0369147258',
        'jennifer_d'
    ),
    (
        '369 Ash Way, Phoenix, AZ 85001',
        'default.jpg',
        'William Jones',
        '$2a$12$93pjT2iLWasrz1U.8S1fzOAWca/BGdFbnBLx8rmOycAhaqmzn76Uq', -- "123456"
        '0741852963',
        'william_j'
    ),
    (
        '741 Beech Path, San Diego, CA 92101',
        'default.jpg',
        'Mary Anderson',
        '$2a$12$93pjT2iLWasrz1U.8S1fzOAWca/BGdFbnBLx8rmOycAhaqmzn76Uq', -- "123456"
        '0963852741',
        'mary_a'
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
    (10, 1);

INSERT INTO houses
(address, bathrooms, bedrooms, description, house_name, price, host_id)
VALUES
    ( '36 Hang Be, Hoan Kiem', 2, 3, 'Fully-equipped apartment, centrally located, suitable for couples or small families.', 'Luxury apartment near Hoan Kiem Lake', 800000, 2),
    ('12 Tran Phu, Ba Dinh', 3, 4, 'Spacious villa with garden, perfect for large families or groups.', 'Elegant Ba Dinh Villa', 1500000, 2),
    ('25 Xuan Dieu, Tay Ho', 2, 2, 'Lake-view apartment with modern design, ideal for expats and digital nomads.', 'Tay Ho Lakeview Apartment', 900000, 2),
    ('78 Kim Ma, Ba Dinh', 1, 1, 'Cozy studio in central Hanoi, close to embassies and business areas.', 'Kim Ma Cozy Studio', 600000, 2),
    ('9 Hang Bong, Hoan Kiem', 2, 3, 'Traditional-style home in Hanoi Old Quarter, offering an authentic experience.', 'Old Quarter Charm House', 850000, 2),
    ('105 Lang Ha, Dong Da', 2, 2, 'Modern apartment with full amenities, near shopping malls and offices.', 'Lang Ha Modern Condo', 950000, 3),
    ('45 Le Van Luong, Thanh Xuan', 2, 3, 'Newly built apartment with gym and pool access.', 'Thanh Xuan Luxury Condo', 1000000, 3),
    ('33 Hoang Hoa Tham, Ba Dinh', 1, 2, 'Quiet and comfortable apartment near West Lake.', 'West Lake Retreat', 700000, 3),
    ('56 Doi Can, Ba Dinh', 3, 4, 'Spacious home with rooftop terrace, perfect for family gatherings.', 'Doi Can Family House', 1200000, 3),
    ('15 Phan Dinh Phung, Hoan Kiem', 2, 3, 'Historic French colonial house with elegant decor.', 'French Colonial Residence', 1400000, 3);

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

insert into availabilities (start_date, end_date, house_id)
values
-- House 1
    ('2025-03-01', '2025-04-15', 1),
    ('2025-05-01', '2025-06-30', 1),
    ('2025-09-30', '2027-10-10', 1),

-- House 2
    ('2025-02-15', '2025-03-31', 2),
    ('2025-04-10', '2025-05-31', 2),
    ('2025-08-31', '2027-10-10', 2),

-- House 3
    ('2025-03-15', '2025-04-30', 3),
    ('2025-06-01', '2025-07-15', 3),
    ('2025-10-31', '2027-10-10',3),

-- House 4
    ('2025-02-01', '2025-03-15', 4),
    ('2025-05-15', '2025-06-30', 4),
    ('2025-09-15', '2027-10-10', 4),

-- House 5
    ('2025-04-01', '2025-05-31', 5),
    ('2025-07-15', '2025-08-31', 5),
    ('2025-11-30', '2027-10-10', 5),

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

insert into bookings (start_date, end_date, status, updated_at, house_id)
values
    -- House 1 (gaps: Apr 16-Apr 30, Jul 1-Sep 29)
    ('2025-04-16', '2025-04-30', 'CONFIRMED', '2025-03-16 14:30:00', 1),
    ('2025-07-01', '2025-09-29', 'CONFIRMED', '2025-06-01 09:45:00', 1),

-- House 2 (gaps: Apr 1-Apr 9, Jun 1-Aug 30)
    ('2025-04-01', '2025-04-09', 'CONFIRMED', '2025-03-01 11:00:00', 2),
    ('2025-06-01', '2025-08-30', 'CONFIRMED', '2025-05-01 13:15:00', 2),

-- House 3 (gaps: May 1-May 31, Jul 16-Oct 30)
    ('2025-05-01', '2025-05-31', 'CONFIRMED', '2025-04-01 15:45:00', 3),
    ('2025-07-16', '2025-10-30', 'CONFIRMED', '2025-06-16 12:00:00', 3),

-- House 4 (gaps: Mar 16-May 14, Jul 1-Sep 14)
    ('2025-03-16', '2025-05-14', 'CONFIRMED', '2025-02-16 14:20:00', 4),
    ('2025-07-01', '2025-09-14', 'CONFIRMED', '2025-06-01 16:45:00', 4),

-- House 5 (gaps: Jun 1-Jul 14, Sep 1-Nov 29)
    ('2025-06-01', '2025-07-14', 'CONFIRMED', '2025-05-01 10:15:00', 5),
    ('2025-09-01', '2025-11-29', 'CONFIRMED', '2025-08-01 13:30:00', 5),

-- House 6 (gaps: May 1-Jun 14, Aug 1-Oct 30)
    ('2025-05-01', '2025-06-14', 'CONFIRMED', '2025-04-01 11:20:00', 6),
    ('2025-08-01', '2025-10-30', 'PENDING', '2025-07-01 15:00:00', 6),

-- House 7 (gaps: Apr 16-Apr 30, Jun 16-Sep 29)
    ('2025-04-16', '2025-04-30', 'CONFIRMED', '2025-03-16 09:00:00', 7),
    ('2025-06-16', '2025-09-29', 'PENDING', '2025-05-16 14:45:00', 7),

-- House 8 (gaps: May 16-May 31, Aug 1-Oct 14)
    ('2025-05-16', '2025-05-31', 'CONFIRMED', '2025-04-16 16:15:00', 8),
    ('2025-08-01', '2025-10-14', 'PENDING', '2025-07-01 10:45:00', 8),

-- House 9 (gaps: Jun 1-Jul 14, Sep 1-Nov 14)
    ('2025-06-01', '2025-07-14', 'CONFIRMED', '2025-05-01 11:30:00', 9),
    ('2025-09-01', '2025-11-14', 'PENDING', '2025-08-01 15:45:00', 9),

-- House 10 (gaps: May 1-May 31, Aug 1-Nov 29)
    ('2025-05-01', '2025-05-31', 'CONFIRMED', '2025-04-01 14:00:00', 10),
    ('2025-08-01', '2025-11-29', 'PENDING', '2025-07-01 10:30:00', 10);
