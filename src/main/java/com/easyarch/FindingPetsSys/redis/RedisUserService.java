package com.easyarch.FindingPetsSys.redis;

import com.easyarch.FindingPetsSys.dto.UserDetailDto;
import com.easyarch.FindingPetsSys.entity.User;

public interface RedisUserService {
    String PREFIX = "FP::";

    UserDetailDto queryUserInfoByUserId(Long userId);

    User queryUserByUserEmail(String email);

    void deleteUserInfoByUserId(Long userId);

    void deleteUserByUserEmail(String email);
}
