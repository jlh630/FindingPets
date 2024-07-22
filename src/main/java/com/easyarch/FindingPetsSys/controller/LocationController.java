//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.controller;

import com.easyarch.FindingPetsSys.dto.LocationIDetailDto;
import com.easyarch.FindingPetsSys.dto.Result;
import com.easyarch.FindingPetsSys.entity.Location;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.service.LocationService;
import com.easyarch.FindingPetsSys.util.UserContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/locations"})
public class LocationController {
    @Autowired
    private LocationService locationService;

    public LocationController() {
    }

    @PostMapping({""})
    public Result<String> saveLocation(@RequestBody Location location) throws NotFoundException {
        return Result.created(locationService.saveLocation(location), null);
    }

    @GetMapping({"/{petId}/current"})
    public Result<LocationIDetailDto> nowLocation(@PathVariable Long petId) throws NotFoundException {
        return Result.success(locationService.nowLocation(petId, UserContext.getUser().getUserId()));
    }

    @GetMapping({"/{petId}/today"})
    public Result<List<LocationIDetailDto>> toDayLocation(@PathVariable Long petId) throws NotFoundException {
        return Result.success(locationService.toDayLocations(petId, UserContext.getUser().getUserId()));
    }
}
