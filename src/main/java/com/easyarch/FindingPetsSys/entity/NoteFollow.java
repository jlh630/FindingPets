package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteFollow {
    private Long userId;
    private Long noteId;
    private Byte status;
    private Long petId;
    private BigDecimal revenue;
    private Date timestamp;
}
