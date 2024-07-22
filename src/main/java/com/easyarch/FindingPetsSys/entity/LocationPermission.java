package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationPermission {
    private Long userId;
    private Long petId;
}
