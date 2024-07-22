package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.UserRole;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper {
    int insertUserRole(@Param("userRole") UserRole userRole);

    List<UserRole> selectUserRolesByUserId(@Param("userId") Long userId);

    List<UserRole> selectUserRolesByRoleId(@Param("roleId") Long roleId);

    List<UserRole> selectUserRolesByUserIds(@Param("userIds") List<Long> userIds);
}
