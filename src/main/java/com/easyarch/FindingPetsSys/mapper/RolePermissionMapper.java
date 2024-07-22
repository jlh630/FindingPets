package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.RolePermission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper {
    List<RolePermission> selectRolePermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);

    List<RolePermission> selectRolePermissionsByRoleId(@Param("roleId") Long roleId);

    List<RolePermission> selectRolePermissionsByPermissionId(@Param("permissionId") Long permissionId);
}
