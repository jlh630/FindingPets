package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.LocationPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LocationPermissionMapper {
    LocationPermission queryLocationPermission(@Param("userId") Long userId, @Param("petId") Long prtId);

    int insertLocationPermission(@Param("userId") Long userId, @Param("petId") Long petId);

    int deleteLocationPermission(@Param("userId") Long userId, @Param("petId") Long petId);

    int deleteAllLocationPermission(@Param("petId") Long petId);
}
