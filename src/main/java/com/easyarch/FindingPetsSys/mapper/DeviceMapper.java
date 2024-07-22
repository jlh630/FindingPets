package com.easyarch.FindingPetsSys.mapper;

import com.easyarch.FindingPetsSys.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceMapper {
    int updateDevice(@Param("device") Device device);

    Device queryDeviceByDeviceId(@Param("id") Long id);

    Device queryDeviceByDeviceCode(@Param("code") String code);
}
