package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Permission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PermissionMapper {
    int insertPermission(@Param("permission") Permission permission);

    int updatePermission(@Param("permission") Permission permission);

    int deletePermission(@Param("permissionId") Long permissionId);

    int queryPermission(@Param("permissionId") Long permissionId);

    List<Permission> selectPermissionsByPermissionIds(@Param("ids") List<Long> ids);
}
