//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service;

import com.easyarch.FindingPetsSys.dto.LocationIDetailDto;
import com.easyarch.FindingPetsSys.entity.Location;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import java.util.List;

public interface LocationService {
    String saveLocation(Location location) throws NotFoundException;

    List<LocationIDetailDto> toDayLocations(Long petId, Long userId) throws NotFoundException;

    LocationIDetailDto nowLocation(Long petId, Long userId) throws NotFoundException;
}
