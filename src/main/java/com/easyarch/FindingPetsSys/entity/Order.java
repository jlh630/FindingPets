package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private Long orderId;
    private Byte status;
    private Long noteId;
    private Long userId;
    private BigDecimal deposit;
    private BigDecimal finalPayment;
    private Date timestamp;
}
