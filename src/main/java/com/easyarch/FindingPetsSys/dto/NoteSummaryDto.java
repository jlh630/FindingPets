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

public class NoteSummaryDto {
    private Long noteId;
    private String userName;
    private String userImg;
    private String title;
    private String summary;
    private BigDecimal reward;
    private boolean owner;
    private boolean isFollow;
    private List<String> resourcePath;
    private Date timestamp;
}
