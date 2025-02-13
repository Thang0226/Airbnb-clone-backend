package com.codegym.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usersTemporary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class User_Temporary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên người dùng (bắt buộc)
    @Column(length = 255)
    private String name;

    // Email (bắt buộc, duy nhất)
    @Column(length = 255, unique = true)
    private String email;

    // Mật khẩu (bắt buộc)
    @Column( length = 255)
    private String password;

    // Số điện thoại
    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 50)
    private String role;

    // Thời gian tạo (bắt buộc)
    @Column
    private LocalDateTime createdAt;

    // Thời gian cập nhật
    @Column()
    private LocalDateTime updatedAt;

    // Constructors
    public User_Temporary() {
    }

    public User_Temporary(String name, String email, String password, String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}
