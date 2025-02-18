package com.codegym.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "host_requests")
public class HostRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String status;
}
