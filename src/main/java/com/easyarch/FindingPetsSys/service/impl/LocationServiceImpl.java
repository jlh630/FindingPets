//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import com.easyarch.FindingPetsSys.dto.LocationIDetailDto;
import com.easyarch.FindingPetsSys.entity.Device;
import com.easyarch.FindingPetsSys.entity.Location;
import com.easyarch.FindingPetsSys.entity.Pet;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.mapper.DeviceMapper;
import com.easyarch.FindingPetsSys.mapper.LocationMapper;
import com.easyarch.FindingPetsSys.mapper.LocationPermissionMapper;
import com.easyarch.FindingPetsSys.mapper.PetMapper;
import com.easyarch.FindingPetsSys.service.LocationService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationMapper locationMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private LocationPermissionMapper locationPermissionMapper;

    public LocationServiceImpl() {
    }

    /**
     * 插入位置信息
     *
     * @param location 位置实体
     * @return 信息
     * @throws NotFoundException 未找到异常
     */
    @Transactional
    public String saveLocation(Location location) throws NotFoundException {
        Long deviceId = location.getDeviceId();
        Device device = deviceMapper.queryDeviceByDeviceId(deviceId);
        if (device != null && device.isStatus()) {
            location.setTimestamp(location.getTimestamp());
            location.setPetId(device.getPetId());
            this.locationMapper.insertLocation(location);
            return "插入成功";
        } else {
            throw new NotFoundException("错误的设备号、设备号不可用");
        }
    }

    /**
     * 今日宠物位置信息
     *
     * @param petId  宠物id
     * @param userId 用户id
     * @return 今日位置信息集合
     * @throws NotFoundException 未找到异常
     */
    public List<LocationIDetailDto> toDayLocations(Long petId, Long userId) throws NotFoundException {
        Pet pet = petMapper.queryPetByPetIdAndUserId(userId, petId);

        if (pet != null && pet.getDeviceId() != null) {
            return locationMapper.selectTodayLocationsByPetId(petId).stream()
                    .map((obj) -> new LocationIDetailDto(obj.getLongitude(), obj.getLatitude()))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("错误宠物号或宠物没有绑定设备");
        }
    }

    /**
     * 当前宠物位置信息
     *
     * @param petId  宠物id
     * @param userId 用户id
     * @return 当前位置信息
     * @throws NotFoundException 未找到异常
     */
    public LocationIDetailDto nowLocation(Long petId, Long userId) throws NotFoundException {
        Pet pet = petMapper.queryPetByPetIdAndUserId(userId, petId);
        //没有权限|错误id有误
        if ((pet == null || pet.getDeviceId() == null) && locationPermissionMapper.queryLocationPermission(userId, petId) == null) {
            throw new NotFoundException("错误宠物号或宠物没有绑定设备");
        } else {
            Location location = locationMapper.queryMaxLocationByPetId(petId);
            return location == null ? null : new LocationIDetailDto(location.getLongitude(), location.getLatitude());
        }
    }
}
