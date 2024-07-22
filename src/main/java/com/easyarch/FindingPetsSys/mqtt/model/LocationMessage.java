package com.easyarch.FindingPetsSys.mqtt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocationMessage {
    private int status;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String time;
    private int satelliteNum;
}
