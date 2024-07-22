package com.easyarch.FindingPetsSys.email.mdoel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {
    private String recipient;
    private String subject;
    private String content;
    private Integer handeType;
    private List<String> resourcePaths;

}

