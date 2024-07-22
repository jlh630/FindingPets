package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandleUserDto {
    private String email;
    private String password;
    private String username;
    private String Code;
}
