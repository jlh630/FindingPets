package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailDto {
    private Long orderId;
    private String status;
    private Long noteId;
    private BigDecimal deposit;
    private BigDecimal finalPayment;
    private Date timestamp;
}
