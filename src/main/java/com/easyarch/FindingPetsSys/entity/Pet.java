package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Pet {
    private Long petId;
    private String petName;
    private String info;
    private Long userId;
    private String url;
    private Long deviceId;
}
