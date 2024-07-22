package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Device {
    private Long deviceId;
    private String deviceName;
    private boolean status;
    private Long petId;
    private String code;
}
