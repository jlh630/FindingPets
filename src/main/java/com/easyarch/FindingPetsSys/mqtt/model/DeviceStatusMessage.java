package com.easyarch.FindingPetsSys.mqtt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceStatusMessage {
    private int code;
    private String data;
}
