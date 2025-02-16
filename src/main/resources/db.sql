DROP PROCEDURE IF EXISTS get_user_profile_by_username;
create procedure get_user_profile_by_username(in _username varchar(255))
begin
    select u.username as username, u.avatar as avatar, u.full_name as fullName, u.address as address, u.phone as phone
    from users u
    where u.username = _username;
end;



# Change database collate to case-sensitive comparing with varchar
ALTER TABLE users MODIFY username VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
