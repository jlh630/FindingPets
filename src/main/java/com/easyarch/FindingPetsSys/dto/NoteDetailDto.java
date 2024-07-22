package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteDetailDto {
    private Long noteId;
    private String userName;
    private String userImg;
    private String title;
    private String content;
    private List<String> resourcePath;
    private BigDecimal reward;
    private boolean visibility;
    private boolean owner;
    private boolean follow;
    private Date timestamp;
}
