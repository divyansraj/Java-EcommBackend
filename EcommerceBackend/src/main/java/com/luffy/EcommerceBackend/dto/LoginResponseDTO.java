package com.luffy.EcommerceBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoginResponseDTO {
    private String name;
    private String email;
    private String role;
}
