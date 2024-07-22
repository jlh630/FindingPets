package com.easyarch.FindingPetsSys.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaDto {
    private String captchaImgBase64;
    private String code;
}
