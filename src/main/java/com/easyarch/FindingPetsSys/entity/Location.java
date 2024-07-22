package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Location {
    private Long locationId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Date timestamp;
    private Long deviceId;
    private Long petId;
}
