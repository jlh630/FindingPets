package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PetDetailDto {
    private Long petId;
    private String petName;
    private String info;
    private String url;
    private boolean isBind;
}
