//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.dto.UserSummaryDto;

import java.util.List;

public interface PetDetectiveService {
    List<UserSummaryDto> selectPetDetectiveId(Long userId);

    boolean isPetDetectiveRoleByUserId(Long userId);

    boolean isPetDetectiveRolesByUserIds(List<Long> userIds,Long userId);
}
