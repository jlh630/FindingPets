package com.easyarch.FindingPetsSys.redis.impl;

import com.easyarch.FindingPetsSys.dto.UserDetailDto;
import com.easyarch.FindingPetsSys.entity.Permission;
import com.easyarch.FindingPetsSys.entity.Role;
import com.easyarch.FindingPetsSys.entity.RolePermission;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.entity.UserRole;
import com.easyarch.FindingPetsSys.mapper.PermissionMapper;
import com.easyarch.FindingPetsSys.mapper.RoleMapper;
import com.easyarch.FindingPetsSys.mapper.RolePermissionMapper;
import com.easyarch.FindingPetsSys.mapper.UserMapper;
import com.easyarch.FindingPetsSys.mapper.UserRoleMapper;
import com.easyarch.FindingPetsSys.redis.RedisUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisUserServiceImpl implements RedisUserService {
    private static final Logger log = LoggerFactory.getLogger(RedisUserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    public RedisUserServiceImpl() {
    }

    /**
     * redis 根据用户id，查找用户信息。
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Cacheable(
            value = PREFIX + "userInfo",
            key = "#userId",
            cacheManager = "cacheManager2H",//过期时间2h
            unless = "#result==null" //不缓存空值
    )
    public UserDetailDto queryUserInfoByUserId(Long userId) {
        User user = this.userMapper.queryUserByUserId(userId);
        //roleIds
        List<Long> roleIds = userRoleMapper.selectUserRolesByUserId(userId).stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        //roles
        List<Role> roles = roleMapper.selectRolesByRoleIds(roleIds);
        //permissionIds
        List<Long> permissionIds = rolePermissionMapper.selectRolePermissionsByRoleIds(roleIds).stream()
                .map(RolePermission::getPermissionsId)
                .collect(Collectors.toList());
        //permission
        List<Permission> permissions = permissionIds.isEmpty() ? new ArrayList<>() : this.permissionMapper.selectPermissionsByPermissionIds(permissionIds);

        return new UserDetailDto(
                user.getUserId(),
                user.getEmail(),
                user.getUserName(),
                user.getImgUrl(),
                "",
                roles,
                permissions);
    }

    /**
     * redis 根据用户邮箱查找。
     *
     * @param email 用户邮箱
     * @return 用户实体
     */
    @Cacheable(
            value = PREFIX + "user",
            key = "#email",
            cacheManager = "cacheManager2H",
            unless = "#result==null"
    )
    public User queryUserByUserEmail(String email) {

        return userMapper.queryUserByUserEmail(email);
    }

    /**
     * redis 根据用户id 删除用户信息
     *
     * @param userId 用户id
     */
    @CacheEvict(
            value = PREFIX + "userInfo",
            key = "#userId",
            condition = "#userId!=null"
    )
    public void deleteUserInfoByUserId(Long userId) {
        log.info("redis del key:{} success", PREFIX + "userInfo::" + userId);
    }

    /**
     * redis 根据用户邮箱，删除用户实体
     *
     * @param email 用户邮箱
     */
    @CacheEvict(
            value = PREFIX + "user",
            key = "#email",
            condition = "#email!=null"
    )
    public void deleteUserByUserEmail(String email) {
        log.info("redis del key:{} success", PREFIX + "user::" + email);
    }
}
