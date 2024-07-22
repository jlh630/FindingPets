package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDto {
    public String userImg;
    public String userName;
    public Long userId;
}
