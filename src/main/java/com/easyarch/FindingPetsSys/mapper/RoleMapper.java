package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Role;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {
    int insertRole(@Param("role") Role role);

    int updateRole(@Param("role") Role role);

    int deleteRole(@Param("id") Long id);

    List<Role> selectRolesByRoleIds(@Param("ids") List<Long> ids);
}
