package com.easyarch.FindingPetsSys.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Note {
    private Long noteId;
    private Long userId;
    private Long petId;
    private String title;
    private String summary;
    private String content;
    private String resourcePath;
    private BigDecimal reward;
    private boolean visibility; //对外可见
    private Date timestamp;
    private boolean publicly; //帖子性质  公开|私密

}
