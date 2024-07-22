package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteFollowDetailDto {
    private Long noteId;
    private String status;
    private Long petId;
    private BigDecimal revenue;
    private Date timestamp;
}
