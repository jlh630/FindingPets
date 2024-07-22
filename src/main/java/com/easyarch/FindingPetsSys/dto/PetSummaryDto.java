package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PetSummaryDto {
    private Long petId;
    private String petName;
    private String url;
}
