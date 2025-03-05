package com.codegym.model.dto.host;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostChatDTO {
    private Long id;
    private String username;
    private String role;
}
