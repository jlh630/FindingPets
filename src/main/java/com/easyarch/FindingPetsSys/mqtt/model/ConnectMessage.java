package com.easyarch.FindingPetsSys.mqtt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConnectMessage {
    private boolean connectStatus;
    private String clientId;
    private long timestamp;
    private String reason;
}
