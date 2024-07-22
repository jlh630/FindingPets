//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import com.easyarch.FindingPetsSys.dto.UserSummaryDto;
import com.easyarch.FindingPetsSys.entity.UserRole;
import com.easyarch.FindingPetsSys.mapper.UserMapper;
import com.easyarch.FindingPetsSys.mapper.UserRoleMapper;
import com.easyarch.FindingPetsSys.service.PetDetectiveService;
import com.easyarch.FindingPetsSys.util.UserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetDetectiveServiceImpl implements PetDetectiveService {
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private UserMapper userMapper;

    public PetDetectiveServiceImpl() {
    }

    /**
     * 除自己以外的petDetective角色的用户id列表
     *
     * @param userId userid
     * @return 用户id集合
     */
    public List<UserSummaryDto> selectPetDetectiveId(Long userId) {
        List<Long> userIds = userRoleMapper.selectUserRolesByRoleId(3L).stream()
                .map(UserRole::getUserId)
                .filter((id) ->
                        !id.equals(userId))
                .collect(Collectors.toList());
        return userIds.isEmpty() ? new ArrayList<>() : userMapper.selectUsersByUserIds(userIds).stream()
                .map(user -> new UserSummaryDto(user.getImgUrl(), user.getUserName(), user.getUserId()))
                .collect(Collectors.toList());

    }

    /**
     * @param userId
     * @return
     */
    public boolean isPetDetectiveRoleByUserId(Long userId) {
        return (userRoleMapper.selectUserRolesByUserId(3L).stream()
                .map(UserRole::getUserId)
                .filter((id) ->
                        !id.equals(UserContext.getUser().getUserId())).collect(Collectors.toList())).contains(userId);
    }

    /**
     * 验证每个userId是否都是petDetective角色
     *
     * @param userIds 待验证userid集合
     * @param userId  用户id
     * @return Y/N
     */
    public boolean isPetDetectiveRolesByUserIds(List<Long> userIds, Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectUserRolesByUserIds(userIds).stream()
                .filter((obj) ->
                        obj.getRoleId() == 3L && !Objects.equals(obj.getUserId(), userId))
                .collect(Collectors.toList());
        return userRoles.size() == userIds.size();
    }
}
